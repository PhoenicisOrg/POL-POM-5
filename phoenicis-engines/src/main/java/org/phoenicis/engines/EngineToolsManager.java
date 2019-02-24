/*
 * Copyright (C) 2015-2017 PÂRIS Quentin
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

package org.phoenicis.engines;

import org.phoenicis.repository.dto.ApplicationDTO;
import org.phoenicis.repository.dto.CategoryDTO;
import org.phoenicis.repository.dto.RepositoryDTO;
import org.phoenicis.repository.dto.TypeDTO;
import org.phoenicis.scripts.interpreter.InteractiveScriptSession;
import org.phoenicis.scripts.interpreter.ScriptInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * manages the engine tools
 */
public class EngineToolsManager {
    private final ScriptInterpreter scriptInterpreter;

    /**
     * constructor
     * @param scriptInterpreter
     */
    public EngineToolsManager(ScriptInterpreter scriptInterpreter) {
        this.scriptInterpreter = scriptInterpreter;
    }

    /**
     * runs a tool in a given container
     * @param engineId ID of the engine which provides the tool (e.g. "Wine")
     * @param container name of the container
     * @param toolId ID of the tool
     * @param doneCallback callback executed after the script ran
     * @param errorCallback callback executed in case of an error
     */
    public void runTool(String engineId, String container, String toolId, Runnable doneCallback,
            Consumer<Exception> errorCallback) {
        final InteractiveScriptSession interactiveScriptSession = scriptInterpreter.createInteractiveSession();

        interactiveScriptSession.eval(
                "include(\"engines." + engineId + ".tools." + toolId + "\");",
                ignored -> interactiveScriptSession.eval("new Tool()", output -> {
                    final EngineTool toolObject = (EngineTool) output;
                    toolObject.run(container);
                    doneCallback.run();
                }, errorCallback), errorCallback);
    }

    /**
     * fetches the available engine tools
     * @param repositoryDTO
     * @param callback
     */
    public void fetchAvailableEngineTools(RepositoryDTO repositoryDTO, Consumer<Map<String, ApplicationDTO>> callback) {
        Map<String, ApplicationDTO> tools = new HashMap<>();
        // get engine CategoryDTOs
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (TypeDTO typeDTO : repositoryDTO.getTypes()) {
            if (typeDTO.getId().equals("engines")) {
                categoryDTOS = typeDTO.getCategories();
            }
        }
        for (CategoryDTO engine : categoryDTOS) {
            for (ApplicationDTO applicationDTO : engine.getApplications()) {
                if (applicationDTO.getId().equals(engine.getId() + ".tools")) {
                    tools.put(engine.getId().replaceAll("^.*\\.", ""), applicationDTO);
                }
            }
        }
        callback.accept(tools);
    }
}
