import de.thatsich.map.MapConverterService;
import de.thatsich.map.URLEncoderConverterService;

module map.converter.service {
	requires java.desktop;
	requires org.apache.logging.log4j;

	exports de.thatsich.map;
	provides MapConverterService with URLEncoderConverterService;
}
