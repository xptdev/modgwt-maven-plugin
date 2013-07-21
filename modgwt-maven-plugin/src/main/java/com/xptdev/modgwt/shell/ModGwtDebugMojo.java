package com.xptdev.modgwt.shell;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;

import java.io.IOException;

import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * @goal                          gwt-debug
 * @execute                       phase=process-classes goal:war:exploded
 * @requiresDirectInvocation
 * @requiresDependencyResolution  test
 */
public class ModGwtDebugMojo extends ModGwtRunMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            Plugin gwtPlugin = getGwtPlugin();

            if (gwtPlugin != null) {

                scanAndIncludeExtraSources();

                executeMojo(getGwtPlugin(), goal("debug"), (Xpp3Dom) gwtPlugin.getConfiguration(),
                    executionEnvironment(project, session, pluginManager));
            } else {
                getLog().error("gwt-maven-plugin not found in project plugins!");
            }

        } catch (IOException | XmlPullParserException e) {
            getLog().error(e);
        }
    }

}