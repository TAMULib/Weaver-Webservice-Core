/*The MIT License (MIT)

Copyright (c) 2015 Stanislav Miklik

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package wro4jBoot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;

public class Wro4jCustomXmlModelManagerFactory extends
		ConfigurableWroManagerFactory {
	private static final Logger log = LoggerFactory
			.getLogger(Wro4jCustomXmlModelManagerFactory.class);
	
	final private Properties props;

	public Wro4jCustomXmlModelManagerFactory(Properties props) {
		this.props = props;
	}
	
	@Override
	protected Properties newConfigProperties() {
		return props;
	}

	@Override
	protected WroModelFactory newModelFactory() {
		log.debug("loading from /wro.xml");
		return new XmlModelFactory() {
			@Override
			protected InputStream getModelResourceAsStream() throws IOException {
				String resourceLocation = "/wro.xml";
				log.info("Loading resource {}", resourceLocation);
				final InputStream stream = getClass().getResourceAsStream(
						resourceLocation);

				if (stream == null) {
					throw new IOException("Invalid resource requested: "
							+ resourceLocation);
				}

				return stream;
			}
		};
	}

}
