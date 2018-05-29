# Web security 101
### Lab Description

Lab 09 contains code examples for:
1. XSS  (Cross-site script injection)
2. CSP  (Content security policy)
3. XSRF (Cross-site request forgery)
5. JWT  (Json Web Token)
6. DoS  (Denial of service)
7. SQLi & NoSQLi (Injection)

These examples are bad (academic purpose only). Really bad. Unbelievably bad. Yuge security risks. Don't use them in your apps... srsly.

## XSS
### Description
Cross-site script injection occurs because input / output is not being sanitized properly or at all. The attacker achieves remote code execution within other users browsers.

Both FE and BE can implement safeguards against it:
1. FE SHOULD sanitize output if necessary
2. FE can sanitize input
3. BE SHOULD sanitize input
4. BE can sanitize output

For our exercise, the following attack vector can be used:
```
<img src='test' onerror='(function(e){alert("Pwned")}).call(this)'>
```

### Resources
* [Code based on this](https://markatta.com/codemonkey/blog/2016/04/18/chat-with-akka-http-websockets/)
* [List of naughty strings](https://github.com/minimaxir/big-list-of-naughty-strings)
* [OWASP](https://www.owasp.org/index.php/Cross-site_Scripting_(XSS))

## CSP
### Description
The content-security-policy allows us to instruct the browser (if supported), how it should treat dynamic resources.

Our attack vector consists of:
* A css and a js resource
* An img with an 'onerror' event
* An inline script
### Resources
* [CSP Home](https://content-security-policy.com/)

## JWT
### Description
A json web token is a way of representing certain claims between parties. Access the resource links for more informations. It has multiple advantages, such as:
* Self containment
* Small in size

Our attack vector consists of modifying the cookie containing the token and taking advantage of an unsafe usage case of a JWT library.
### Resources
* [JWT Home 1](https://jwt.io)
* [JWT Home 2](https://www.jsonwebtoken.io/)

## XSRF
### Description
### Resources

## DoS
### Description
### Resources

## SQLi & NoSQLi
### Description
### Resources

# Tips & Resources
Please keep the following things in mind:
* Stay up-to-fucking-date with all your dependencies!
* Hide your shitty version headers or anything that exposes what technologies you're using
* Use HTTPS for everyting (no mixed content with HTTP) including web sockets
* Don't expose more than necessary
* Pay attention to your server / libraries configuration
* Log and monitor! Detect breaches & unexpected behaviour early

