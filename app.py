import flask
from flask_restx import Api,Resource

import os 
from flask import jsonify
from flask import request,render_template
from flask import send_file
import io

from werkzeug.utils import secure_filename
from werkzeug.datastructures import ImmutableMultiDict
from werkzeug.datastructures import FileStorage
from services import image
from services import db
from services import api
from flask import make_response




app = flask.Flask(__name__)



    
    
@app.route('/main/post/', methods=['GET', 'POST'])
def upload_file():
    controller = db.db()
    preprocess = image.Process()
    if request.method == 'POST':
        print(flask.request.files.get('image'))
    #     # print(flask.request.form)
        file_dir = "C:/Python/pilling_server/media/"
        kakaoId = flask.request.form.get('title')   # 누가 찍었는지 판단하는 카카오아이디
        f2 = flask.request.files.get('image')       #클라이언트에서 보낸 사진
        f2.save(file_dir+(f2.filename))
    
        
        preprocess.preprocess((file_dir+(f2.filename)),f2.filename)
        ocrlist = preprocess.bestmatch(f2.filename)
        print(ocrlist)
        controller.registerPrescription(f2.filename,kakaoId)
        controller.insertMedecineInfo(f2.filename,ocrlist)
        os.remove(file_dir+(f2.filename))
        
    return {"file":"a"}

@app.route('/downloadImage/<url>')
def download_image(url):
    # 이미지 파일 경로 설정
    image_path = "C:/Python/pilling_server/media/crop/"+url
    return send_file(image_path, mimetype='image/jpg')
    
    return {"file":"a"}

@app.route('/imagedata/<kakaoId>')
def get_files(kakaoId):
    print("카카오아이디 : "+kakaoId)
    controller = db.db()
    data=controller.getPrescription(kakaoId)

    # print(data)
    return data

@app.route('/druginfo/<prescription>')
def get_drug(prescription):
    print("처방전 : "+prescription)
    controller = db.db()
    data=controller.getDrugInfo(prescription)
    
    print("데이터 ",data)
    return data
          
@app.route('/')
def come():
      
    return {"a" : "hi"}

@app.route("/alarm",methods=['GET', 'POST'])
def post():
    controller = db.db()

    if(request.method == 'POST'):
        alarm_data = request.json
        kakaoId = alarm_data.get('kakaoId')
        hour = alarm_data.get('hour')
        min = alarm_data.get('min')
        requestCode = alarm_data.get('requestCode')
        controller.insertAlarm(kakaoId,hour,min,requestCode)
        
        return {"file":"a"}
    
@app.route("/requestcode", methods=['POST'])
def getRequestCode():
    controller = db.db()

    if(request.method == 'POST'):
        alarm_data = request.json
        print(alarm_data)
        
        controller.deleteAlarm(alarm_data)
        # response = {'requestCode': requestCode}

        # return jsonify(response)
        
        return {"file":"a"}
    
@app.route("/prescription", methods=['POST'])
def deletePrescription():
    controller = db.db()

    if(request.method == 'POST'):
        prescription_data = request.json
        print(prescription_data)
        
        controller.deletePrescription(prescription_data)
        
        return {"file":"a"}
    
@app.route('/alarm/<kakaoId>')
def get_alarm(kakaoId):
    print("카카오아이디 : "+kakaoId)
    controller = db.db()
    data=controller.getAlarm(kakaoId)

    # print(data)
    return data

@app.route('/alarm/count/<kakaoId>')
def maxrequestCode(kakaoId):
    print("카카오아이디 : "+kakaoId)
    controller = db.db()
    data=controller.getMax(kakaoId)

    # print(data)
    return data
if __name__=="__main__":
    app.run(host = '127.0.0.1', port=8000,debug=True)