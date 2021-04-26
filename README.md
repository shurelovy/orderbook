Aggregation of the book streams from two websockets - Kraken and Bitfinex.

Bitfinex
https://docs.bitfinex.com/docs/ws-general

Kraken
https://docs.kraken.com/websockets/

Print out to console the orderbook on every server update, along with the best bid and ask.

To run it, please execute following maven commands: 
* mvn compile
* mvn exec:java -Dexec.mainClass="com.exchange.Orderbook"