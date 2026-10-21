#!/usr/bin/env python
import json
from flask import Flask, jsonify, request

app = Flask(__name__)
all_banks=['SWEDBANK','IKANOBANKEN','JPMORGAN','NORDEA','CITIBANK','HANDELSBANKEN','SBAB','HSBC','NORDNET']

@app.route('/bank/list', methods=['GET'])
def List_all_banks() -> json:   
    with open('accounts.json') as banks:
        banks = json.load(banks)
    
    return banks

@app.route('/bank/find.name', methods=['GET'])
def find_bank_by_name() -> json:
    name = request.args.get('name')
    with open('accounts.json') as banks:
        banks = dict(json.load(banks))
        if name in banks.values():
            key = ""
            for k in banks.keys():
                if banks[k] == name:
                    key = k
            return jsonify({"key": key, "name": name})        
    return jsonify(None)

# @app.route('/bank/find.key', methods=['GET'])
# def find_bank_by_key()-> str:
#     key = request.args.get('key')
#     with open('accounts.json') as banks:
#         banks = dict(json.load(banks))
#         if key in banks.keys():
#             return jsonify({"key": key, "name":banks[key]})
        
#     return jsonify(None)

@app.route('/bank/find.key', methods=['GET'])
def find_bank_by_key() -> json:
    key = request.args.get('key')
    with open('accounts.json') as f:
        banks = json.load(f)
    if key in banks:
        return jsonify({"key": key, "name": banks[key]})
    return jsonify(None)

if __name__ == '__main__':
    app.run(debug=True, port=8070)
# print(List_all_banks)