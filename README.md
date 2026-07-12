#  Public PoC: IITK IDOR Vulnerability

> **Disclaimer:** The DDIA has claimed to have fixed this vulnerability, so making the PoC public is no harm to anyone and will benefit cybersecurity aspirants to understand where flaws can lie even in big institutions, IRONICALLY the ones who have launched a whole new degree for this particular field. This repo is completely for EDUCATIONAL purpose and no harm is intended. I bear no responsibility for any misuse of this as I've already reported this PoC, and apparently the bug is fixed.

---

## Overview
This vulnerability leaks data during the payments process where server is requested to create an order for an applicant with certain Integer id (for example:- 819)




## Setup and Usage

1. Install IntelliJ IDEA IDE (It was used for this)
2. Navigate to Maven pom.xml and install the dependencies
3. In Main.java you'll find the sample code to work with
4. Optionally, For observing the traffic you can start burp suite and Set Up the ProxySelector setup in Main.java file to route the requests through burp suite.
5. Run the Main.java file

### Configuring the Proxy in `Main.java`
You have to change the `ProxySelector` variable like this in `Main.java` file and ensure the proxy settings are pointing to your Burp Suite listener (default is usually `127.0.0.1:8080`).

```java
static {
        proxySelector = ProxySelector.of(
                new InetSocketAddress("localhost", 8080)
        );
}
```


### Example output
```aiignore
UserDatIITK{applicant_name='Example', mobile_no='6969696969', email_id='example69@mail.com'}
```
