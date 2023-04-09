<h1 style="text-align: center; color: green; -webkit-animation: rainbow 5s infinite; -moz-animation: rainbow 5s infinite; -o-animation: rainbow 5s infinite; animation: rainbow 5s infinite;">ChatGPT Spring</h1>

## ChatGPT spring boot application 

gpt3, gpt4 supported.

Built in Chat API, web socket, context management

### How to use

Clone the project to your local machine
```
git clone project_url
```

Open `application.properties` file, set up your own configuration
Here is the sample:

```shell
# proxy = http://127.0.0.1:7890
open_ai_key = sk-xxx
max_token = 3000
timeout = 10000
open_ai_host = https://api.openai.com/
open_ai_model = gpt-3.5-turbo
redis_host = localhost
redis_port = 6379
#redis_password = 1234
```

Start your project
```
mvn spring-boot:run
```

To call the API
```shell
curl -X POST http://localhost:8080/app/chat?text=?text=your text&mid=yourmid&cid=yourcid
```

There are three query parameters you can use.

`text` (required): Your prompts. <br></br>
`mid`: the message id you want to respond to. <br></br>
`cid`: conversation id (session id). <br></br>

Response sample
```json
{
    "text": "Sure, what do you wonder about? I'm here to help you find answers to your questions or provide assistance in any way I can.",
    "messageId": "b095773b-e207-4cca-a6ee-14cb417c3c02",
    "parentMessageId": "3693b64f-33f0-441a-9627-927f7c0cccaa",
    "conversationId": "e0c9fe58-bd2d-4f95-a431-e95955b2661c"
}
```

### Future work

Web socket support.