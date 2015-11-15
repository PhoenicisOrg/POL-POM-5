#!/usr/bin/env python
# coding=utf-8

# Copyright (C) 2015 Pâris Quentin

# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

import os

from Environment.EnvironmentFormatter import EnvironmentFormatter

from com.playonlinux.framework import Downloader
from com.playonlinux.core.scripts import ScriptFailureException
from com.playonlinux.framework import WineVersion
from com.playonlinux.framework import Wine
from com.playonlinux.framework import WineShortcut

from com.playonlinux.core.utils import Architecture

from java.net import URL
from java.io import File, FileOutputStream

class CommandParser(object):
    def __init__(self, setupWindowManager, command):
        self.command = command
        self.splitCommand = self.command.split("\t")
        self.setupWindowManager = setupWindowManager

    def getCookie(self):
        return self.splitCommand[0]

    def getCommand(self):
        return self.splitCommand[1]

    def executeCommand(self):
        commandExecutor = CommandParser.CommandExecutor(self.splitCommand, self.setupWindowManager)
        return getattr(commandExecutor, self.getCommand())()

    class CommandExecutor():
        def __init__(self, command, setupWindowManager):
            self.command = command
            self.setupWindowManager = setupWindowManager

        def POL_SetupWindow_Init(self):
            setupWindowId = self.command[2]
            if("TITLE" in os.environ.keys()):
                windowTitle = os.environ["TITLE"]
            else:
                windowTitle = "${application.name} Wizard";

            self.setupWindowManager.newWindow(setupWindowId, windowTitle).init()

        def POL_SetupWindow_message(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]

            self.setupWindowManager.getWindow(setupWindowId).message(textToShow)
        
        def POL_SetupWindow_presentation(self):
            setupWindowId = self.command[2]
            programName = self.command[3]
            programEditor = self.command[4]
            editorURL = self.command[5]
            scriptorName = self.command[6]
            prefixName = self.command[7]
            
            self.setupWindowManager.getWindow(setupWindowId).presentation(programName, programEditor, editorURL, scriptorName, prefixName)
        
        def POL_SetupWindow_free_presentation(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]
            
            self.setupWindowManager.getWindow(setupWindowId).presentation(textToShow)

        def POL_SetupWindow_wait(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]

            self.setupWindowManager.getWindow(setupWindowId).wait(textToShow)

        def POL_SetupWindow_browse(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]

            try:
                currentDirectory = self.command[4]
            except IndexError:
                currentDirectory = ""

            return self.setupWindowManager.getWindow(setupWindowId).browse(textToShow, currentDirectory, allowedFiles)


        def POL_SetupWindow_textbox(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]

            try:
                defaultValue = self.command[4]
            except IndexError:
                defaultValue = ""

            return self.setupWindowManager.getWindow(setupWindowId).textbox(textToShow, defaultValue)

        def POL_SetupWindow_menu(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]

            try:
                separator = self.command[5]
            except IndexError:
                separator = "~"

            items = self.command[4].split(separator)
            return self.setupWindowManager.getWindow(setupWindowId).menu(textToShow, items)

        def POL_SetupWindow_Close(self):
            setupWindowId = self.command[2]

            self.setupWindowManager.getWindow(setupWindowId).close()

        def POL_Download(self):
            setupWindowId = self.command[2]
            url = self.command[3]
            currentDirectory = self.command[4]

            try:
                checkSum = self.command[5]
            except IndexError:
                checkSum = ""

            setupWindow = self.setupWindowManager.getWindow(setupWindowId)

            localFile = os.path.join(currentDirectory,
                                     Downloader.wizard(setupWindow).findFileNameFromURL(URL(url)))


            downloader = Downloader.wizard(setupWindow).get(url, localFile)


            if(checkSum != ""):
                downloader.check(checkSum)
        
        def POL_SetupWindow_licence(self):
            setupWindowId = self.command[2]
            textToShow = self.command[3]
            licenceFilePath = self.command[5]
            
            self.setupWindowManager.getWindow(setupWindowId).licenceFile(textToShow, licenceFilePath)

        def POL_Throw(self):
            raise ScriptFailureException(self.command[3])

        def POL_Print(self):
            message = self.command[3]
            self.setupWindowManager.template.log(message)

        def POL_Wine_InstallVersion(self):
            setupWindowId = self.command[2]
            version = self.command[3]
            arch = self.command[4]

            wineVersion = WineVersion(version, "upstream-%s" % arch,
                                                self.setupWindowManager.getWindow(setupWindowId))
            wineVersion.install()

        def POL_Wine_PrefixCreate(self):
            setupWindowId = self.command[2]
            setupWindow = self.setupWindowManager.getWindow(setupWindowId)
            prefixName = self.command[3]
            version = self.command[4]

            try:
                arch = self.command[5]
                arch = str(Architecture.fromWinePackageName(arch).name())
            except IndexError:
                arch = None

            if(arch is not None):
                Wine.wizard(setupWindow).selectPrefix(prefixName).createPrefix(version, "upstream", arch)
            else:
                Wine.wizard(setupWindow).selectPrefix(prefixName).createPrefix(version, arch)


        def POL_Wine(self):
            setupWindowId = self.command[2]
            setupWindow = self.setupWindowManager.getWindow(setupWindowId)
            workingDirectory = self.command[3]
            prefixName = self.command[4]
            fifoOut = self.command[5]
            fifoErr = self.command[6]
            env = EnvironmentFormatter.getEnvironmentVarsFromBashBase64EncodedString(self.command[7])
            prgmName = self.command[8]
            args = self.command[9::1]

            return Wine.wizard(setupWindow).selectPrefix(prefixName)\
                .withErrorStream(FileOutputStream(File(fifoErr)))\
                .withOutputStream(FileOutputStream(File(fifoOut)))\
                .runForeground(
                    workingDirectory,
                    prgmName,
                    args,
                    env
                ).getLastReturnCode()


        def POL_Config_PrefixRead(self):
            setupWindowId = self.command[2]
            setupWindow = self.setupWindowManager.getWindow(setupWindowId)

            prefixName = self.command[3]
            key = self.command[4]

            return Wine.wizard(setupWindow).selectPrefix(prefixName).config().readValue(key)


        def POL_Config_PrefixWrite(self):
            setupWindowId = self.command[2]
            setupWindow = self.setupWindowManager.getWindow(setupWindowId)

            prefixName = self.command[3]
            key = self.command[4]
            value = self.command[5]

            return Wine(setupWindow).selectPrefix(prefixName).config().writeValue(key, value)

        def POL_Shortcut(self):
            setupWindowId = self.command[2]
            setupWindow = self.setupWindowManager.getWindow(setupWindowId)

            winePrefix = self.command[3]
            binary = self.command[4]
            shortcutName = self.command[5]
            websiteIcon = self.command[6]
            argument = self.command[7]
            categories = self.command[8]

            WineShortcut.wizard(setupWindow)\
                .withArguments([argument])\
                .withExecutableName(binary)\
                .withWinePrefix(winePrefix)\
                .withName(shortcutName)\
                .create()