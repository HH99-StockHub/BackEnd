from flask import Flask

app = Flask(__name__)

from pymongo import MongoClient
import certifi

ca = certifi.where()
client = MongoClient('mongodb+srv://gaius365:123qweasd@cluster0.wyemi.mongodb.net/?retryWrites=true&w=majority')
db = client.stockhub

index = {"indexName": "S&P 500"}
db.indices.insert_one(index)

index = {"indexName": "Dow Jones Industrial Average"}
db.indices.insert_one(index)

index = {"indexName": "NASDAQ Composite"}
db.indices.insert_one(index)

index = {"indexName": "FTSE 100"}
db.indices.insert_one(index)

index = {"indexName": "Nikkei 225"}
db.indices.insert_one(index)

index = {"indexName": "HANG SENG INDEX"}
db.indices.insert_one(index)

index = {"indexName": "KOSPI Composite Index"}
db.indices.insert_one(index)

print("전체 인덱스 저장 완료")

exit()

if __name__ == '__main__':
    app.run('0.0.0.0', port=5000, debug=True)