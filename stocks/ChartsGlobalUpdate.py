from pykiwoom.kiwoom import *
from flask import Flask
import datetime
import time

app = Flask(__name__)

from pymongo import MongoClient
import certifi

ca = certifi.where()
client = MongoClient('mongodb+srv://gaius365:123qweasd@cluster0.wyemi.mongodb.net/?retryWrites=true&w=majority')
db = client.stockhub

# OpenAPI+ 로그인
kiwoom = Kiwoom()
kiwoom.CommConnect(block=True)

# 전체 종목 불러오기
chartList = list(db.charts.find({}))

# 기준일자 얻기
now = datetime.datetime.now()
today = now.strftime("%Y%m%d")

# 전체 종목 차트 업데이트
for chart in chartList:
    stockCode = chart["stockCode"]
    df = kiwoom.block_request("opt10081", 종목코드=stockCode, 기준일자=today, 수정주가구분=1, output="주식일봉차트조회", next=0)
    rows = df.shape[0]
    arr = []
    if rows > 250:
        rows = 250
    if rows == 1: continue
    for i in range(0, rows):
        date = df.iloc[i]["일자"]
        firstPrice = int(df.iloc[i]["시가"])
        highPrice = int(df.iloc[i]["고가"])
        lowPrice = int(df.iloc[i]["저가"])
        lastPrice = int(df.iloc[i]["현재가"])
        arr.append([date, firstPrice, highPrice, lowPrice, lastPrice])
    db.charts.update_one({"stockCode": stockCode}, {"$set": {"chart": arr}})
    time.sleep(3.6)

print("전체 종목 차트 업데이트 완료")

exit()

if __name__ == '__main__':
    app.run('0.0.0.0', port=5000, debug=True)
