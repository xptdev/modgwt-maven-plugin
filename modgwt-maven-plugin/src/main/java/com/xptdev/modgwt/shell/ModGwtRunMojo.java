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

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

import com.xptdev.modgwt.util.MavenUtils;

/**
 * Goal which run a GWT module in the GWT Hosted mode.
 *
 * @goal                          gwt-run
 * @execute                       phase=process-classes goal:war:exploded
 * @requiresDirectInvocation
 * @requiresDependencyResolution  test
 * @description                   Runs the the project in the GWT Hosted mode for development.
 */
public class ModGwtRunMojo extends AbstractMojo {

    /**
     * The Maven Project Object.
     *
     * @parameter  expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * The Maven Project Object.
     *
     * @parameter  expression="${executedProject}"
     * @required
     * @readonly
     */
    protected MavenProject execProject;

    /**
     * The Maven Session Object.
     *
     * @parameter  expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;

    /**
     * The Maven PluginManager Object.
     *
     * @component
     * @required
     */
    protected PluginManager pluginManager;

    /**
     * <p>top folder from which we will scan for additional artifacts</p>
     *
     * @parameter  default-value="." expression="${modgwt.search}" @required
     */
    protected File searchRoot;

    /**
     * <p>additional artifacts that should be translated to js for hosted mode</p>
     *
     * @parameter  expression="${modgwt.includes}"
     */
    protected String includes;

    /* (non-Javadoc)
     * @see org.apache.maven.plugin.AbstractMojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {

        try {

            Plugin gwtPlugin = getGwtPlugin();

            if (gwtPlugin != null) {

                scanAndIncludeExtraSources();

                ExecutionEnvironment env;
                try {
                    Object o = session.lookup("org.apache.maven.plugin.BuildPluginManager");

                    env = executionEnvironment(project, session, (BuildPluginManager) o);
                } catch (ComponentLookupException e) {
                    env = executionEnvironment(project, session, pluginManager);
                }

                executeMojo(getGwtPlugin(), goal("run"), (Xpp3Dom) gwtPlugin.getConfiguration(), env);
            } else {
                getLog().error("gwt-maven-plugin not found in project plugins!");
            }

        } catch (IOException | XmlPullParserException e) {
            getLog().error(e);
        }
    }

    /**
     * Gets the gwt plugin.
     *
     * @return  the gwt plugin
     */
    protected Plugin getGwtPlugin() {

        List<Plugin> plugins = project.getBuildPlugins();

        for (Plugin plugin : plugins) {
            String id = plugin.getArtifactId();

            if (id.equalsIgnoreCase("gwt-maven-plugin")) {

                return plugin;
            }
        }

        return null;
    }

    /**
     * Scan and include extra sources.
     *
     * @throws  IOException             Signals that an I/O exception has occurred.
     * @throws  XmlPullParserException  the xml pull parser exception
     */
    protected void scanAndIncludeExtraSources() throws IOException, XmlPullParserException {
        if (includes == null) {
            getLog().info("No include source artifacts defined!");
            return;
        }

        String[] artifacts = includes.split(",");

        if (artifacts.length == 0) {
            getLog().info("No include source artifacts defined!");
            return;
        }

        for (String str : artifacts) {
            Path pom = MavenUtils.findChildPom(str.trim(), searchRoot.toPath());

            if (pom != null) {
                includeExtraGwtSources(pom);
            } else {
                getLog().info(str + " module not found!");
            }
        }

    }

    /**
     * Include extra gwt sources.
     *
     * @param   pom  the pom
     *
     * @throws  IOException             Signals that an I/O exception has occurred.
     * @throws  XmlPullParserException  the xml pull parser exception
     */
    protected void includeExtraGwtSources(final Path pom) throws IOException, XmlPullParserException {

        Model model = MavenUtils.parseMavenModel(pom);

        String src = model.getBuild().getSourceDirectory();
        String id = model.getArtifactId();

        if (src == null) {
            src = "src/main/java/";
        }

        String sourceFolder = pom.getParent().toFile().getAbsolutePath() + File.separator + src;
        if (new File(sourceFolder).exists()) {
            execProject.addCompileSourceRoot(sourceFolder);
            getLog().info("Added source folder " + sourceFolder + " from " + id);
        } else {
            getLog().info("Source folder for" + id + " not found!");
        }

        List<Resource> resources = model.getBuild().getResources();

        if (resources == null || resources.size() == 0) {
            String rsrc = "src/main/resources/";
            String resourceFolder = pom.getParent().toFile().getAbsolutePath() + File.separator + rsrc;
            if (new File(resourceFolder).exists()) {
                Resource resource = new Resource();
                resource.setDirectory(resourceFolder);
                execProject.addResource(resource);
                getLog().info("Added resource folder " + resource.getDirectory() + " from " + id);
            } else {
                getLog().info("Resource folder for" + id + " not found!");
            }
        } else {
            for (Resource resource : resources) {
                execProject.addResource(resource);
                getLog().info("Added resource folder " + resource.getDirectory() + " from " + id);

            }
        }

    }

}
