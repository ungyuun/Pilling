from services import api
import re
import pymysql
import datetime
import json
from flask import Flask, jsonify

class db:
    def insertMedecineInfo(self,img,pill):
        print("img : "+img)
        getapi = api.api()
        
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
        # cursor.execute('SELECT * FROM main_test;')
        # value = cursor.fetchall()
        # print(value)
        # print(pill[0])
        
        for i in range(len(pill)):
            print("for문 ")
            cursor.execute('INSERT INTO ocr_tb VALUES ("'+img+'","'+pill[i]+'");')
            naverResult = getapi.naver_api(pill[i])
            newText = re.sub('(<([^>]+)>)', '', naverResult["items"][0]["title"])
            print(newText)
            if newText== None:
                continue
            apiResult = getapi.openapi(newText)
            if (apiResult==False):
                
                # print('INSERT INTO medecine_info VALUES ("'+newText+'","'+pill[i]+'","'+naverResult["items"][0]['thumbnail']+'","'+naverResult["items"][0]['link']+'",NULL,NULL,NULL,NULL);')
                cursor.execute('INSERT INTO medecine_info VALUES ("'+newText+'","'+pill[i]+'","'+naverResult["items"][0]['thumbnail']+'","'+naverResult["items"][0]['link']+'",NULL,NULL,NULL,NULL);')    
                
            else:
                cursor.execute('INSERT INTO medecine_info VALUES ("'+newText+'","'+pill[i]+'","'+naverResult["items"][0]['thumbnail']+'","'+naverResult["items"][0]['link']+'","'+str(apiResult['CLASS_NAME'])+'","'+str(apiResult['DRUG_SHAPE'])+'","'+str(apiResult['COLOR_CLASS1'])+'","'+str(apiResult['COLOR_CLASS2'])+'");')
            
        db.commit()
        db.close()
        
        return {"good"}
    
    def registerPrescription(self,img,kakaoId):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        now = datetime.datetime.now()
        now_date = now.strftime('%Y-%m-%d')
    
        cursor.execute('INSERT INTO prescription_tb VALUES ("'+img+'","'+kakaoId+'",NULL,"'+now_date+'");')
        db.commit()
        db.close()
        
        return {"good"}
    
    def getPrescription(self,kakaoId):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        
    
        cursor.execute('SELECT * FROM prescription_tb WHERE kakao_Id = "'+kakaoId+'";')
        ret = cursor.fetchall()
        
        json_list = json.dumps(ret)
        parsed_list = json.loads(json_list)
       
        json_data = json.dumps([{
            "prescription": item[0],
            "kakaoId": item[1],
            "subject": item[2],
            "date": item[3]
        } for item in parsed_list])
        print(json_data)
       
        db.commit()
        db.close()
        
        return json_data
    
    def getDrugInfo(self,prescription):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        
    
        cursor.execute('SELECT m.medecine_nm,m.thumbnail,m.link,m.efficacy,m.shape,m.color1,m.color2 FROM ocr_tb AS o, medecine_info AS m WHERE (o.ocr_nm = m.ocr_nm) AND o.prescription = "'+prescription+'" GROUP BY m.ocr_nm')
        ret = cursor.fetchall()
        print("ret : ",ret)
        json_list = json.dumps(ret, ensure_ascii=False)
        print("json_list :",json_list)
        parsed_list = json.loads(json_list)
        
        
       
        json_data = json.dumps([{
            "medecine": item[0],
            "thumbnail": item[1],
            "link": item[2],
            "efficacy": item[3],
            "shape": item[4],
            "color1": item[5],
            "color2": item[6]
        } for item in parsed_list], ensure_ascii=False)
        # print(json_data)
       
        db.commit()
        db.close()
        
        return json_data
    
    def insertAlarm(self,kakaoId,hour,min,requestCode):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        
        sql = 'INSERT INTO alarm_tb VALUES ({}, "{}", "{}", "{}", 1);'.format(requestCode, kakaoId, hour, min)
        cursor.execute(sql)
        db.commit()
        db.close()
        
        return {"good"}
    
    def deleteAlarm(self,requestCode):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        
       
        sql = 'DELETE FROM alarm_tb WHERE requestCode = ({})'.format(requestCode)
        cursor.execute(sql)
        db.commit()
        db.close()
        
        return {"good"}
    
    def deletePrescription(self,prescription):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
        sql = 'DELETE FROM prescription_tb WHERE prescription = "'+prescription+'";'
        cursor.execute(sql)
        db.commit()
        db.close()
        
        return {"good"}
    
    def getAlarm(self,kakaoId):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        
    
        cursor.execute('SELECT * FROM alarm_tb WHERE kakaoId = "'+kakaoId+'";')
        ret = cursor.fetchall()
        
        json_list = json.dumps(ret)
        parsed_list = json.loads(json_list)
       
        json_data = json.dumps([{
            "requestCode": item[0],
            "kakaoId": item[1],
            "hour": item[2],
            "min": item[3],
            "act": bool(item[4])
        } for item in parsed_list])
        print(json_data)
       
        db.commit()
        db.close()
        
        
        return json_data
    
    def getMax(self,kakaoId):
        db = pymysql.connect(host="localhost",user="sungyuun",password="sungyuun",charset="utf8")
        cursor = db.cursor()
        cursor.execute('USE pilling;')
    
        
    
        cursor.execute('SELECT requestCode FROM alarm_tb WHERE kakaoId = "'+kakaoId+'" AND requestCode = (SELECT max(requestCode) from alarm_tb);')
        ret = cursor.fetchall()
        
        json_list = json.dumps(ret)
        parsed_list = json.loads(json_list)
        value = parsed_list[0][0] 
        json_data = json.dumps({"requestCode": value})
       
        db.commit()
        db.close()
        
        return json_data
        
    
    
    
    