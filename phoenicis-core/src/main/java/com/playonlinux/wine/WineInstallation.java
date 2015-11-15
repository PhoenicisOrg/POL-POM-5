/*
 * Copyright (C) 2015 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.playonlinux.wine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playonlinux.core.version.Version;
import com.playonlinux.engines.wine.WineDistribution;

public class WineInstallation {
    private static final String WINEPREFIXCREATE_COMMAND = "wineboot";
    private static final String WINEPREFIX_ENV = "WINEPREFIX";

    /* Disbles winemenubuilder */
    private static final String WINEDLLOVERRIDES_ENV = "WINEDLLOVERRIDES";
    private static final String DISABLE_WINEMENUBUILDER = "winemenubuilder.exe=d";

    private final File binaryPath;
    private final File libraryPath;
    private final Map<String, String> applicationEnvironment;
    private final WineDistribution distribution;
    private final Version version;

    private WineInstallation(WineInstallation.Builder builder) {
	this.binaryPath = new File(builder.path, "bin");
	this.libraryPath = new File(builder.path, "lib");
	this.applicationEnvironment = builder.applicationEnvironment;
	this.version = builder.version;
	this.distribution = builder.distribution;
    }

    // TODO
    public String fetchVersion() {
	return null;
    }

    private File fetchWine() {
	return fetchExecutable(binaryPath, "wine");
    }

    private File fetchWineServer() {
	return fetchExecutable(binaryPath, "wineserver");
    }

    private File fetchExecutable(File path, String name) {
	File executable = new File(path, name);
	if (executable.exists() && !executable.canExecute()) {
	    executable.setExecutable(true);
	}
	return executable;
    }

    // FIXME: Maybe it would be great to createPrefix a class to handle
    // environment issues
    private void addPathInfoToEnvironment(Map<String, String> environment) {
	environment.put("PATH", this.binaryPath.getAbsolutePath());
	environment.put("LD_LIBRARY_PATH", this.libraryPath.getAbsolutePath());
    }

    public Process run(WinePrefix winePrefix, File workingDirectory, String executableToRun,
	    Map<String, String> environment, List<String> arguments) throws WineException {

	/* Sets the wineprefix */
	final Map<String, String> winePrefixEnvironment = new HashMap<>();
	winePrefixEnvironment.put(WINEPREFIX_ENV, winePrefix.getAbsolutePath());

	/* Disbles winemenubuilder */
	winePrefixEnvironment.put(WINEDLLOVERRIDES_ENV, DISABLE_WINEMENUBUILDER);

	final List<String> command = new ArrayList<>();
	command.add(this.fetchWine().getAbsolutePath());
	command.add(executableToRun);
	if (arguments != null) {
	    command.addAll(arguments);
	}

	final Map<String, String> wineEnvironment = new HashMap<>();
	if (environment != null) {
	    wineEnvironment.putAll(environment);
	}
	wineEnvironment.putAll(winePrefixEnvironment);

	this.addPathInfoToEnvironment(wineEnvironment);

	try {
	    return new WineProcessBuilder().withCommand(command).withEnvironment(wineEnvironment)
		    .withWorkingDirectory(workingDirectory).withApplicationEnvironment(applicationEnvironment).build();
	} catch (IOException e) {
	    throw new WineException(e);
	}
    }

    public Process createPrefix(WinePrefix winePrefix) throws WineException {
	winePrefix.createConfigFile(this.distribution, this.version);
	return this.run(winePrefix, winePrefix.getWinePrefixDirectory(), WINEPREFIXCREATE_COMMAND);
    }

    public Process run(WinePrefix winePrefix, File workingDirectory, String executableToRun,
	    Map<String, String> environment) throws WineException {
	return this.run(winePrefix, workingDirectory, executableToRun, environment, null);
    }

    private Process run(WinePrefix winePrefix, File workingDirectgory, String executableToRun) throws WineException {
	return this.run(winePrefix, workingDirectgory, executableToRun, null);
    }

    public void killAllProcess(WinePrefix winePrefix) throws IOException {
	runWineServerCommand(winePrefix, "-k");
    }

    public void waitAllProcesses(WinePrefix winePrefix) throws IOException {
	runWineServerCommand(winePrefix, "-w");
    }

    private void runWineServerCommand(WinePrefix winePrefix, String parameter) throws IOException {
	final Map<String, String> environment = new HashMap<>();
	this.addPathInfoToEnvironment(environment);
	environment.put(WINEPREFIX_ENV, winePrefix.getAbsolutePath());

	final List<String> command = new ArrayList<>();
	command.add(this.fetchWineServer().getAbsolutePath());
	command.add(parameter);

	new WineProcessBuilder().withCommand(command).withEnvironment(environment).build();

    }

    public boolean exists() {
	return this.binaryPath.exists() && this.libraryPath.exists();
    }

    public WineDistribution getDistribution() {
	return distribution;
    }

    public Version getVersion() {
	return version;
    }

    public static class Builder {
	private File path;
	private Map<String, String> applicationEnvironment;
	private Version version;
	private WineDistribution distribution;

	public WineInstallation.Builder withVersion(Version version) {
	    this.version = version;
	    return this;
	}

	public WineInstallation.Builder withDistribution(WineDistribution distributionName) {
	    this.distribution = distributionName;
	    return this;
	}

	public WineInstallation.Builder withPath(File path) {
	    this.path = path;
	    return this;
	}

	public WineInstallation.Builder withApplicationEnvironment(Map<String, String> applicationEnvironment) {
	    this.applicationEnvironment = applicationEnvironment;
	    return this;
	}

	public WineInstallation build() {
	    return new WineInstallation(this);
	}

    }

}
