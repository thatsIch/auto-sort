module autosort {
	requires org.apache.logging.log4j;
	requires commons.cli;
	requires java.prefs;

	requires map.converter.service;
	uses de.thatsich.map.MapConverterService;
	requires unification.service;
}
