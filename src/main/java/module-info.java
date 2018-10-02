
module httpserver {
	exports com.vmanolache.httpserver.api;
    exports com.vmanolache.httpserver.api.constants;

    requires static lombok;
    requires org.apache.logging.log4j;
}