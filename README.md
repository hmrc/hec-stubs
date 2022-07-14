
# hec-stubs

The stubs in this repository are stubs to enable the [hec-applicent-frontend](https://github.com/hmrc/hec-applicant-frontend)
service to work without the underlying dependencies.

# Test Data

## Company Data

The table below describes responses to a number of API's. The journeys for companies that you will be able to trigger on the applicant
frontend service will be rooted in the company registration number (CRN). The columns should be interpreted like so:
- The `Company Name` column describes the response given by the companies house API. Given a particular
  CRN, either a successful response with the given company name will be returned or an error response will be issued
- The `CT UTR` column describes the response given by the DES "Get CT reference" API. This represents what HMRC records
  store as the corporation tax unique tax identifier (CT UTR) for a given CRN. It is used to match a CRN with a CT UTR
- The `CT status` columns describes the response to the CT return status API. The input to the API is the CT UTR of the  
  company
 
If using a stubbed login, then the CT UTR can either be given as an enrolment or entered in as part of the applicant 
frontend journey if the login has not been configured with a CT enrolment with the CT UTR which matches the CRN given 
in the journey.

| CRN      | Company Name                | CT UTR                      | CT status                                |
| -------- |-----------------------------| --------------------------- | ---------------------------------------- |
| `11...`  | `Test Tech Ltd`             | `1111111111`                | return found                             |
| `12...`  | `Test Tech Ltd`             | `2222222222`                | notice to file issued                    |
| `13...`  | `Test Tech Ltd`             | `3333333333`                | no return found                          |
| `14...`  | `Test Tech Ltd`             | `4444444444`                | no accounting periods                    |
| `21...`  | `Test Tech Ltd`             | 404 (Not Found)             | -                                        |
| `22...`  | `Test Tech Ltd`             | 400 (Bad Request)           | -                                        |
| `23...`  | `Test Tech Ltd`             | 500 (Internal Server Error) | -                                        |
| `24...`  | `Test Tech Ltd`             | 503 (Service Unavailable)   | -                                        |
| `31...`  | 404 (Not Found)             | -                           | -                                        |
| `32...`  | 500 (Internal Server Error) | -                           | -                                        |
| `33...`  | 503 (Service Unavailable)   | -                           | -                                        |
| `41...`  | `Test Tech Ltd`             | `9999999999`                | 404 (Not Found)                          |
| `421...` | `Test Tech Ltd`             | `9299999998`                | 400 (Bad Request) invalid CTUTR          |
| `422...` | `Test Tech Ltd`             | `9289999996`                | 400 (Bad Request) invalid start date     |
| `423...` | `Test Tech Ltd`             | `9279999994`                | 400 (Bad Request) invalid end date       |
| `424...` | `Test Tech Ltd`             | `9269999992`                | 400 (Bad Request) invalid correlation ID |
| `43...`  | `Test Tech Ltd`             | `9399999995`                | 422 (Unprocessable Entity)               |
| `44...`  | `Test Tech Ltd`             | `9499999992`                | 500 (Internal Server Error)              |
| `46...`  | `Test Tech Ltd`             | `9699999997`                | 502 (Bad Gateway)                        |
| `47...`  | `Test Tech Ltd`             | `9799999994`                | 503 (Service Unavailable)                |

## Individual Data

The table below describes responses to a number of API's. The journeys for individuals that you will be able to trigger on the applicant
frontend service will be rooted in the national insurance number (NINO) and the self assessment unique tax reference 
(SA UTR) if an SA journey is chosen. The columns should be interpreted like so:
- The `Date of Birth` and `SA UTR` columns are the values that will be returned by the citizen details API stub for the
  given NINO. The SA UTR is an optional field in the response and it is not always returned by the stub. The name that is
  returned by the stub is always the same
- the `SA status` column describes the stub responses to the SA return status API. The input to this API is an SA UTR 

If using a stubbed login, then to be able to use an SA UTR which is different from `1234567895` use an `NS...` NINO in the 
login together with an SA enrolment that contains the desired SA UTR. 


| NINO          | Date of Birth | SA UTR        | SA status                   |
| ------------- | ------------- |---------------| --------------------------- |
| `NS...`       | `01-12-1922`  | -             |  -                          |
| anything else | `01-12-1922`  | `1234567895`  | return found                |
| -             | -             | `1111...`     | no return found             |
| -             | -             | `2222...`     | notice to file issued       |
| -             | -             | `3333...`     | 500 (Internal Server Error) |
| -             | -             | anything else | return found                |

## Email verification data

### Request passcode

The table below describes responses to the "request email passcode" API. The response is triggered by the email address in
the request. The difference responses described in the table have different HTTP status codes and different error code 
strings that form part of the JSON response body in the case of an error.

| email address                      | request passcode response | response error code       | 
| ---------------------------------- | ------------------------- | ------------------------- |
| `no_session_id@email.com`          | 401 (unauthorised)        | `NO_SESSION_ID`           |
| `email_verified_already@email.com` | 409 (conflict)            | `EMAIL_VERIFIED_ALREADY`  |
| `max_emails_exceeded@email.com`    | 403 (forbidden)           | `MAX_EMAILS_EXCEEDED`     |
| `bad_email_request@email.com`      | 400 (bad request)         | `BAD_EMAIL_REQUEST`       |
| `upstream_error@email.com`         | 502 (bad gateway)         | `UPSTREAM_ERROR`          |
| anything else                      | 201 (created)             | -                         |

### Verify passcode

The table below describes responses to the "verify email passcode" API. The response is triggered by the passcode in
the request. The difference responses described in the table have different HTTP status codes and different error code
strings that form part of the JSON response body in the case of an error.

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
