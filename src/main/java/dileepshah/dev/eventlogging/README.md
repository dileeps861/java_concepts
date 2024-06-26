### Demonstrate a multithreaded event logging mechanism

- Log request could be submitted from multiple threads
- Log should be persisted into a log file representing each line as log
- The System should have low latency and high reliability having said that must not lose logs at any cost
- Only should confirm to the client if the logs are persisted successfully 

### Experimentation
- Experimented with 1k threads, and each thread submitting 100 request and queue size is 10k
- Having batch size of equals to thread i.e. 1k improves the latency,avg p90 latency 50ms
- If batch size 10 then avg p90 latency 64ms
- If batch size 10k then avg p90 latency 66ms