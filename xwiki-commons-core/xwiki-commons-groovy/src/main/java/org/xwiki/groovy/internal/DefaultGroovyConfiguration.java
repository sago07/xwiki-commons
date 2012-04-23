/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.groovy.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.groovy.GroovyCompilationCustomizer;
import org.xwiki.groovy.GroovyConfiguration;

/**
 * Default configuration implementation for the Groovy module, especially the list of Groovy Compilation Customizers.
 *
 * @version $Id$
 * @since 4.1M1
 */
@Component
@Singleton
public class DefaultGroovyConfiguration implements GroovyConfiguration
{
    /**
     * Prefix for configuration keys for the Groovy module.
     */
    private static final String PREFIX = "groovy.";

    /**
     * By default we timeout after 1 minute.
     */
    private static final Long SCRIPT_TIMEOUT = 60L;

    /**
     * Defines from where to read the configuration data.
     */
    @Inject
    private ConfigurationSource configuration;

    /**
     * The logger to log.
     */
    @Inject
    private Logger logger;

    /**
     * Used to get Compilation Customizer implementations to use.
     */
    @Inject
    private ComponentManager componentManager;

    @Override
    public List<CompilationCustomizer> getCompilationCustomizers()
    {
        List<CompilationCustomizer> customizers = new ArrayList<CompilationCustomizer>();
        List<String> customizerNames = this.configuration.getProperty(PREFIX + "compilationCustomizers",
            Collections.<String>emptyList());
        for (String customizerName : customizerNames) {
            try {
                GroovyCompilationCustomizer customizer =
                    this.componentManager.getInstance(GroovyCompilationCustomizer.class, customizerName);
                customizers.add(customizer.createCustomizer());
            } catch (Exception e) {
                // Just don't use the customizer but log the error
                this.logger.warn("Failed to create the Groovy Compilation Customizer named [{}]", customizerName, e);
            }
        }
        return customizers;
    }

    @Override
    public long getScriptTimeout()
    {
        return this.configuration.getProperty(PREFIX + "scriptTimeout", SCRIPT_TIMEOUT);
    }
}
