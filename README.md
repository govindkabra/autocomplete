A server code to support prefix searches for Autocomplete API on large datasets.

This is a library to support Autocomplete API for arbitrary dataset. For any
prefix query, it returns top 10 matches in constant time. The system is
available as interactive command line interface, as well as through an embedded
jetty web server.  

It lets you search for prefix of any term in city name, e.g., for "pa", it will
match Paris, and Patna; as well as, Sao Paulo and El Paso. However, it does not
do substring searches, e.g., "pa" would not match Bhopal.

See online demo at (while it lasts): <a href="http://54.201.130.60:8081/city.html">City Autocomplete</a>

At the core of it, it uses a custom TopTrie datastructure to index all the
input names. And stores top 10 matches on each node, using a custom
implementation of priority queue with fixed size.
 
It starts by taking a (space separated) input txt file containing names and
scores. See data dir for some examples. 

Replace spaces in names with underscores. Then generate a protocol buffer bin
file using this python script. 

```
$python src/python/generate_dataset.py data/city-1M.txt data/city-1M.bin
```

Once you have the protobuf format data, you can now start java server. 

For command line interface, run:
```
$java -cp 3rdparty/protobuf-java-2.5.0.jar:./bin/. com/screen/server/InteractiveRanker data/city-1M.bin
```

To start a webserver (listening on port 8081), run:

(psst.. i should have really combined those into a single jar)

```
$java -cp 3rdparty/commons-beanutils-1.8.3.jar:3rdparty/commons-collections-3.2.1.jar:3rdparty/commons-lang-2.6.jar:3rdparty/commons-logging-1.1.3.jar:3rdparty/ezmorph-1.0.6.jar:3rdparty/jetty-continuation-7.6.14.v20131031.jar:3rdparty/jetty-http-7.6.14.v20131031.jar:3rdparty/jetty-io-7.6.14.v20131031.jar:3rdparty/jetty-server-7.6.14.v20131031.jar:3rdparty/jetty-util-7.6.14.v20131031.jar:3rdparty/json-lib-2.4-jdk15.jar:3rdparty/protobuf-java-2.5.0.jar:3rdparty/servlet-api-2.5.jar:./bin/:. com.screen.server.WebserviceRanker data/city-1M.bin
```

If you want to build other interfaces to this system, you can extend TrieRanker
server class, which is what InteractiveRanker and WebserviceRanker do. 

