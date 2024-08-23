# [Secure Your Network Traffic 🚦— Encrypt Your Network Requests 🔐](https://chetan-garg36.medium.com/secure-your-network-traffic-encrypt-your-network-requests-e4a5a682c82a)


## Introduction 🔥
Implementation of encrypt network traffic so that even with a man-in-the-middle attack (i.e., network interception), 
our data remains encrypted and useless to the attacker.

To achieve this, we have used an `Asymmetric encryption` method to generate public and private keys. 

- Public Key : used on the Android/frontend device to encrypt HTTP request payloads,
- Private key : stored on the backend server to decrypt them back into their original form.


### Read Implementation Details from [Here](https://chetan-garg36.medium.com/secure-your-network-traffic-encrypt-your-network-requests-e4a5a682c82a)


## Structure

```text
.
└── encryptedpayloads
    ├── MainActivity.kt
    ├── MainViewmodel.kt
    ├── server
    │   └── EmbeddedServer.kt
    └── ui
        ├── data
        │   ├── ApiManager.kt
        │   ├── EncryptionService.kt
        │   ├── InMemoryDB.kt
        │   └── Model.kt
        └── theme 
```


## MIT License
Copyright (c) 2021 Chetan Gupta

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.




