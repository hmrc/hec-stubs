
# hec-stubs

# Test Data

## Company Data

| CRN      | Company Name (Companies House) | CT UTR                      | CT status                                | 
| -------- | ------------------------------ | --------------------------- | ---------------------------------------- |
| `11...`  | `Test Tech Ltd`                | `1111111111`                | return found                             |      
| `12...`  | `Test Tech Ltd`                | `2222222222`                | notice to file issued                    |      
| `13...`  | `Test Tech Ltd`                | `3333333333`                | no return found                          |      
| `14...`  | `Test Tech Ltd`                | `4444444444`                | no accounting periods                    |      
| `21...`  | `Test Tech Ltd`                | 404 (Not Found)             | -                                        |      
| `22...`  | `Test Tech Ltd`                | 400 (Bad Request)           | -                                        |      
| `23...`  | `Test Tech Ltd`                | 500 (Internal Server Error) | -                                        |      
| `24...`  | `Test Tech Ltd`                | 503 (Service Unavailable)   | -                                        |      
| `31...`  | 404 (Not Found)                | -                           | -                                        |      
| `32...`  | 500 (Internal Server Error)    | -                           | -                                        |      
| `33...`  | 503 (Service Unavailable)      | -                           | -                                        |      
| `41...`  | `Test Tech Ltd`                | `9999999999`                | 404 (Not Found)                          |      
| `421...` | `Test Tech Ltd`                | `9299999998`                | 400 (Bad Request) invalid CTUTR          |      
| `422...` | `Test Tech Ltd`                | `9289999996`                | 400 (Bad Request) invalid start date     |      
| `423...` | `Test Tech Ltd`                | `9279999994`                | 400 (Bad Request) invalid end date       |      
| `424...` | `Test Tech Ltd`                | `9269999992`                | 400 (Bad Request) invalid correlation ID |      
| `43...`  | `Test Tech Ltd`                | `9399999995`                | 422 (Unprocessable Entity)               |
| `44...`  | `Test Tech Ltd`                | `9499999992`                | 500 (Internal Server Error)              |
| `46...`  | `Test Tech Ltd`                | `9699999997`                | 502 (Bad Gateway)                        |
| `47...`  | `Test Tech Ltd`                | `9799999994`                | 503 (Service Unavailable)                |

## Individual Data

| NINO          | Date of Birth | SA UTR (Citizens Details) | SA status                   |
| ------------- | ------------- | ------------------------- | --------------------------- |
| `NS...`       | `01-12-1922`  | -                         |  -                          |
| anything else | `01-12-1922`  | `1234567895`              | return found                | 
| -             | -             | `1111...`                 | no return found             | 
| -             | -             | `2222...`                 | notice to file issued       | 
| -             | -             | `3333...`                 | 500 (Internal Server Error) | 
| -             | -             | anything else             | return found                |      

## Email verification data

### Request passcode

| email address                      | request passcode response | response error code       | 
| ---------------------------------- | ------------------------- | ------------------------- |
| `no_session_id@email.com`          | 401 (unauthorised)        | `NO_SESSION_ID`           |
| `email_verified_already@email.com` | 409 (conflict)            | `EMAIL_VERIFIED_ALREADY`  |
| `max_emails_exceeded@email.com`    | 403 (forbidden)           | `MAX_EMAILS_EXCEEDED`     |
| `bad_email_request@email.com`      | 400 (bad request)         | `BAD_EMAIL_REQUEST`       |
| `upstream_error@email.com`         | 502 (bad gateway)         | `UPSTREAM_ERROR`          |
| anything else                      | 201 (created)             | -                         |

### Verify passcode

| passcode      | verify passcode response | response error code              |
| ------------- | ------------------------ | -------------------------------- |
| `BBBBBB`      | 401 (unauthorised)       | `NO_SESSION_ID`                  | 
| `CCCCCC`      | 403 (forbidden)          | `MAX_PASSCODE_ATTEMPTS_EXCEEDED` |
| `DDDDDD`      | 404 (not found)          | `PASSCODE_NOT_FOUND`             |
| `FFFFFF`      | 404 (not found)          | `PASSCODE_MISMATCH`              | 
| `GGGGGG`      | 204 (no content)         | -                                |
| anything else | 201 (created)            | -                                |


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
