from pykiwoom.kiwoom import *
from flask import Flask
import schedule
import time
from datetime import datetime, timedelta

import yfinance as yf
from pandas_datareader import data as pdr

yf.pdr_override()

import requests
from bs4 import BeautifulSoup

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

# 기준일자 얻기
now = datetime.now() + timedelta(days=1)
then = now - timedelta(days=10)
startDate = then.strftime("%Y-%m-%d")
endDate = now.strftime("%Y-%m-%d")


# 전체 종목 현재가 업데이트
def pricesUpdate():
    for code in kospiCodes:
        ticker = code + ".KS"
        df = pdr.get_data_yahoo(ticker, startDate, endDate)
        if df.empty:
            continue
        rows = df.shape[0]
        stockPrice = int(df.iloc[rows-1]["Close"])
        lastPrice = int(df.iloc[rows-2]["Close"])
        change = stockPrice - lastPrice
        changeRate = round(change / lastPrice * 100, 2)
        startPrice = int(df.iloc[rows-1]["Open"])
        highPrice = int(df.iloc[rows-1]["High"])
        lowPrice = int(df.iloc[rows-1]["Low"])
        tradeVolume = int(df.iloc[rows-1]["Volume"])
        db.stocks.update_one({"stockCode": code}, {
            "$set": {"stockPrice": stockPrice, "change": change, "changeRate": changeRate,
                     "lastPrice": lastPrice, "startPrice": startPrice, "highPrice": highPrice, "lowPrice": lowPrice,
                     "tradeVolume": tradeVolume}})

    for code in kosdaqCodes:
        ticker = code + ".KQ"
        df = pdr.get_data_yahoo(ticker, startDate, endDate)
        if df.empty:
            continue
        rows = df.shape[0]
        stockPrice = int(df.iloc[rows - 1]["Close"])
        lastPrice = int(df.iloc[rows - 2]["Close"])
        change = stockPrice - lastPrice
        changeRate = round(change / lastPrice * 100, 2)
        startPrice = int(df.iloc[rows - 1]["Open"])
        highPrice = int(df.iloc[rows - 1]["High"])
        lowPrice = int(df.iloc[rows - 1]["Low"])
        tradeVolume = int(df.iloc[rows - 1]["Volume"])
        db.stocks.update_one({"stockCode": code}, {
            "$set": {"stockPrice": stockPrice, "change": change, "changeRate": changeRate,
                     "lastPrice": lastPrice, "startPrice": startPrice, "highPrice": highPrice, "lowPrice": lowPrice,
                     "tradeVolume": tradeVolume}})
    print("Prices updated")


# 인덱스 업데이트
def worldIndicesUpdate():
    data = requests.get("https://finance.yahoo.com/world-indices")
    soup = BeautifulSoup(data.text, "html.parser")
    indices = soup.select("#list-res-table > div.Ovx\(a\).Ovx\(h\)--print.Ovy\(h\).W\(100\%\) > table > tbody > tr")

    for index in indices:
        indexName = index.select_one("td.Va\(m\).Ta\(start\).Px\(10px\).Fz\(s\)").text
        lastPrice = index.select_one("td.Va\(m\).Ta\(end\).Pstart\(10px\).Fw\(600\).Fz\(s\) > fin-streamer").text
        change = index.select_one("td:nth-child(4) > fin-streamer > span").text
        changeRate = index.select_one("td:nth-child(5) > fin-streamer > span").text
        db.indices.update_one({"indexName": indexName},
                              {"$set": {"lastPrice": lastPrice, "change": change, "changeRate": changeRate}})
    print("Indices updated")


# 스케쥴러
schedule.every(2).seconds.do(worldIndicesUpdate)
schedule.every(2).seconds.do(pricesUpdate)

while True:
    schedule.run_pending()
    time.sleep(1)

if __name__ == '__main__':
    app.run('0.0.0.0', port=5000, debug=True)
