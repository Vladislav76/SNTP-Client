# SNTP Client

Simple NTP client which:
- sends request to specified NTP-server
- gets response from it 
- prints packet content
- calculates round trip delay, local clock offset and accurate local time

## Launching

### Mac or Linux

```
> ./gradlew run --args=<ntp_server_domain>
```

### Windows

```
> ./gradlew.bat run --args=<ntp_server_domain>
```

### Example

```
> ./gradlew run --args=ntp1.stratum1.ru
> ./gradlew.bat run --args=ntp1.stratum1.ru
```