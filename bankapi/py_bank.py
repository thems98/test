#!/usr/bin/env python
import json
from flask import Flask, request

app = Flask(__name__)
all_banks=['SWEDBANK','IKANOBANKEN','JPMORGAN','NORDEA','CITIBANK','HANDELSBANKEN','SBAB','HSBC','NORDNET']

@app.route('/bank/list', methods=['GET'])
def List_all_banks() -> list:   
    with open('accounts.json') as banks:
        banks = json.load(banks)
    
    return banks

@app.route('/bank/find.name', methods=['GET'])
def find_bank_by_name() -> str:
    name = request.args.get('name')
    with open('accounts.json') as banks:
        banks = dict(json.load(banks))
        if name in banks.values():
            return name
        
    return "null"

@app.route('/bank/find.key', methods=['GET'])
def find_bank_by_KEY()-> str:
    key = request.args.get('key')
    with open('accounts.json') as banks:
        banks = dict(json.load(banks))
        if key in banks.keys():
            return banks[key]
        
    return "null"

if __name__ == '__main__':
    app.run(debug=True, port=8070)
print(List_all_banks)