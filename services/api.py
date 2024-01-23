import sys
import io
sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding = 'utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding = 'utf-8')
import json
import urllib.request
import requests



class api:
    def naver_api(self,ocrname):
        client_id = "0apJILjDB8aBob3dXMkP"
        client_secret = "nu0Yqb2QBg"

        url = "https://openapi.naver.com/v1/search/encyc.json"
        option = "&display=3&sort=count"
        query = "?query="+urllib.parse.quote(ocrname)
        url_query = url + query + option

        request = urllib.request.Request(url_query)
        request.add_header("X-Naver-Client-Id",client_id)
        request.add_header("X-Naver-Client-Secret",client_secret)

        response = urllib.request.urlopen(request)
        rescode = response.getcode()
        if(rescode == 200):
            response_body = response.read()
            retdata = response_body.decode('utf-8')
            jsonResult = json.loads(retdata)
            # print(jsonResult["items"][0]['title'])
            # print(jsonResult)
            # print(jsonResult["items"][0])
            return jsonResult
        else:
            print("Error code:"+rescode)
            
    def openapi(self,api_name):
        url = 'http://apis.data.go.kr/1471000/MdcinGrnIdntfcInfoService01/getMdcinGrnIdntfcInfoList01'
        apikey = 'uZXaZwwNXokZXkD4ASPFQ2oVUKT9vTpLotDdEzr4Gm5wXbN4yPsz21bg1LT6nAXSTMLClP6Ep+ddOl9L0B9mwQ=='   
        api_key_decode = requests.utils.unquote(apikey)
        params ={'serviceKey' : api_key_decode, 'item_name' : api_name,'pageNo' : '1', 'numOfRows' : '1', 'type' : 'json' }

        response = requests.get(url, params=params)
        a = json.loads(response.content.decode())
        # print(a["body"]["items"][0]['CHART'],a["body"]["items"][0]['DRUG_SHAPE'],a["body"]["items"][0]['CLASS_NAME'],a["body"]["items"][0]['COLOR_CLASS1'],a["body"]["items"][0]['COLOR_CLASS2']) 
        # print(a["body"]["items"][0])
        
        if a["body"]["totalCount"]==0:
            return False
        else:
           return a["body"]["items"][0]