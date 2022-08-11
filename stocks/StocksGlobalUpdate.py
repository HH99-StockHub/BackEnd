from pykiwoom.kiwoom import *
from flask import Flask
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
stockList = list(db.stocks.find({}))

# 전체 종목 현재가 업데이트
for stock in stockList:
    stockCode = stock["stockCode"]
    df = kiwoom.block_request("opt10001", 종목코드=stockCode, output="주식기본정보요청", next=0)
    stockPrice = int(df.iloc[0]["현재가"])
    change = int(df.iloc[0]["전일대비"])
    changeRate = float(df.iloc[0]["등락율"])
    lastPrice = int(df.iloc[0]["기준가"])
    startPrice = int(df.iloc[0]["시가"])
    highPrice = int(df.iloc[0]["고가"])
    lowPrice = int(df.iloc[0]["저가"])
    tradeVolume = int(df.iloc[0]["거래량"])
    if stockPrice < 0:
        stockPrice *= -1
    if startPrice < 0:
        startPrice *= -1
    if highPrice < 0:
        highPrice *= -1
    if lowPrice < 0:
        lowPrice *= -1
    db.stocks.update_one({"stockCode": stockCode}, {
        "$set": {"stockPrice": stockPrice, "change": change, "changeRate": changeRate,
                 "lastPrice": lastPrice, "startPrice": startPrice, "highPrice": highPrice, "lowPrice": lowPrice,
                 "tradeVolume": tradeVolume}})
    time.sleep(3.6)

print("전체 종목 현재가 업데이트 완료")

exit()

if __name__ == '__main__':
    app.run('0.0.0.0', port=5000, debug=True)