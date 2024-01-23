import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding = 'utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding = 'utf-8')
import cv2
import re
import numpy as np
import pytesseract
import matplotlib.pyplot as plt



class Process: 
    file_dir = "C:/Python/pilling_server/media/"
    
    def preprocess(self,url,filename):
        
        print("url : "+url)
        print("file : "+filename)
        src = cv2.imread(url, 1)
        gray = cv2.cvtColor(src, cv2.COLOR_BGR2GRAY)
        gray = cv2.GaussianBlur(gray, (3, 3), 0)
        canned = cv2.Canny(gray, 150, 300)
        kernel = np.ones((10,1),np.uint8) # 가로 1 세로 10
        mask = cv2.dilate(canned, kernel, iterations = 20)
        
        # contours 찾기
        contours,_ = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

        # 가장 큰 contours 찾기
        biggest_cntr = None
        biggest_area = 0
        for contour in contours:
            area = cv2.contourArea(contour)
            if area > biggest_area:
                biggest_area = area
                biggest_cntr = contour

        # 외곽 box
        rect = cv2.minAreaRect(biggest_cntr)
        box = cv2.boxPoints(rect)
        box = np.int0(box)

        # 외곽 box 그리기
        src_box = src.copy()
        cv2.drawContours(src_box, [box], 0, (0, 255, 0), 3)

        # angle 계산
        angle = rect[-1]
        if angle > 45:
            angle = -(90 - angle)

        # 기울기 조정
        rotated = src.copy()
        (h, w) = rotated.shape[:2]
        center = (w // 2, h // 2)
        M = cv2.getRotationMatrix2D(center, angle, 1.0)
        rotated = cv2.warpAffine(rotated, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)

        ones = np.ones(shape=(len(box), 1))
        points_ones = np.hstack([box, ones])
        transformed_box = M.dot(points_ones.T).T

        y = [transformed_box[0][1], transformed_box[1][1], transformed_box[2][1], transformed_box[3][1]]
        x = [transformed_box[0][0], transformed_box[1][0], transformed_box[2][0], transformed_box[3][0]]

        y1, y2 = int(min(y)), int(max(y))
        x1, x2 = int(min(x)), int(max(x))

        # crop
        crop =rotated[y1:y2, x1:x2]

        # 흑백처리
        gray2 = cv2.cvtColor(crop, cv2.COLOR_BGR2GRAY)
        canned2 = cv2.Canny(gray2, 150, 300)


        cv2.imwrite(Process.file_dir+"canny/"+filename, canned2)

        cv2.imwrite(Process.file_dir+"crop/"+filename, crop)
        
        
       
        
        

    def ocr(self,filename,qimg):
        qimg = cv2.imread(qimg,0) # queryImage
        timg = cv2.imread(Process.file_dir+'canny/'+filename,0) # trainImage
        oimg = cv2.imread(Process.file_dir+'crop/'+filename,0) # originalImage
        sift = cv2.SIFT_create()
        
        kp1, des1 = sift.detectAndCompute(qimg,None)
        kp2, des2 = sift.detectAndCompute(timg,None)

        FLANN_INDEX_KDTREE = 0
        index_params = dict(algorithm=FLANN_INDEX_KDTREE,trees=5)
        search_params = dict(checks=50)

        flann = cv2.FlannBasedMatcher(index_params,search_params)

        matches = flann.knnMatch(des1,des2,k=2)

        good = []
        for m,n in matches:
            if m.distance < 0.7*n.distance:
                good.append(m)
        MIN_MATCH_COUNT = 5
        if len(good) > MIN_MATCH_COUNT:
            src_pts = np.float32([kp1[m.queryIdx].pt for m in good]).reshape(-1,1,2)
            dst_pts = np.float32([kp2[m.trainIdx].pt for m in good]).reshape(-1,1,2)

            M, mask = cv2.findHomography(src_pts,dst_pts,cv2.RANSAC,5.0)
            matchesMask = mask.ravel().tolist()
            h,w = qimg.shape
            print("height : ",h," width : ",w)
            pts = np.float32([[0,h+30],[0,h+400],[w/5*2,h+400],[w/5*2,h+30]]).reshape(-1,1,2)
            
            dst = cv2.perspectiveTransform(pts,M)
            x1,y1,w1,h1 = (np.int32(dst))
            x = x1[0][0]
            y = x1[0][1]
            w = w1[0][0]
            h = w1[0][1]-x1[0][1]
            roi = oimg[y:y+h, x:x+w]
            cv2.imwrite(Process.file_dir+'ocrimage/'+filename,roi) 
            
            timg = cv2.polylines(timg,[np.int32(dst)],True,255,3,cv2.LINE_AA)
        else:
            print("발견된 매칭이 부족합니다 - %d/%d" % (len(good),MIN_MATCH_COUNT))                                                                                                                                                                                                                    
            matchesMask = None
            
        draw_params = dict(matchColor = (0,255,0),
                        singlePointColor = None,
                        matchesMask=matchesMask, # inliers만 그리기
                        flags=2)
        res=None
        res = cv2.drawMatches(qimg,kp1,timg,kp2,good,res,**draw_params)
        ocr = pytesseract.image_to_string(Process.file_dir+'ocrimage/'+filename,lang="kor")
        
        x = re.findall(r"^(?!.*약품명).*?(?=\n|$)",ocr, re.MULTILINE | re.DOTALL)
        x = list(filter(lambda x: x != '', x))
        

        cv2.waitKey(0)
        cv2.destroyAllWindows()
        
        return x
        
        
        
    def bestmatch(self,filename):
        P = Process()
        get1 = P.getmatch(filename,(P.file_dir+'queryimage/rule1.png'))
        get2 = P.getmatch(filename,(P.file_dir+'queryimage/rule2.png'))
        get3 = P.getmatch(filename,(P.file_dir+'queryimage/rule3.png'))
        
        match = [get1,get2,get3]
        if (max(match)==get1):
            x = P.ocr(filename,P.file_dir+'queryimage/rule1.png')
        if (max(match)==get2):
            x = P.ocr(filename,P.file_dir+'queryimage/rule2.png')
        if (max(match)==get3):
            x = P.ocr(filename,P.file_dir+'queryimage/rule3.png')    
            
        return x
        
        
    def getmatch(self,filename,queryimage):
        qimg = cv2.imread(queryimage,0) # queryImage
        timg = cv2.imread(Process.file_dir+'canny/'+filename,0) # trainImage
        oimg = cv2.imread(Process.file_dir+'crop/'+filename,0) # originalImage
        sift = cv2.xfeatures2d.SIFT_create()

        kp1, des1 = sift.detectAndCompute(qimg,None)
        kp2, des2 = sift.detectAndCompute(timg,None)

        FLANN_INDEX_KDTREE = 0
        index_params = dict(algorithm=FLANN_INDEX_KDTREE,trees=5)
        search_params = dict(checks=50)

        flann = cv2.FlannBasedMatcher(index_params,search_params)

        matches = flann.knnMatch(des1,des2,k=2)

        good = []
        for m,n in matches:
            if m.distance < 0.7*n.distance:
                good.append(m)
                
        return len(good)
        
        
    