from pykiwoom.kiwoom import *
from flask import Flask

app = Flask(__name__)

from pymongo import MongoClient
import certifi

ca = certifi.where()
client = MongoClient('mongodb+srv://gaius365:123qweasd@cluster0.wyemi.mongodb.net/?retryWrites=true&w=majority')
db = client.stockhub

# OpenAPI+ 로그인
kiwoom = Kiwoom()
kiwoom.CommConnect(block=True)

# KOSPI/KOSDAQ 종목 저장
kospiCodes = kiwoom.GetCodeListByMarket('0')
kosdaqCodes = kiwoom.GetCodeListByMarket('10')

stockList = []

for code in kospiCodes:
    name = kiwoom.GetMasterCodeName(code)
    chart = {
        "stockCode": code,
        "stockName": name
    }
    if db.charts.find_one({"stockCode": code}):
        continue
    else:
        db.charts.insert_one(chart)

for code in kosdaqCodes:
    name = kiwoom.GetMasterCodeName(code)
    chart = {
        "stockCode": code,
        "stockName": name
    }
    if db.charts.find_one({"stockCode": code}):
        continue
    else:
        db.charts.insert_one(chart)

print("전체 차트 저장 완료")

exit()

if __name__ == '__main__':
    app.run('0.0.0.0', port=5000, debug=True)
