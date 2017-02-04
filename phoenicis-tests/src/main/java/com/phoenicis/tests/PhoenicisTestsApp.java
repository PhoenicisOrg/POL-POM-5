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

package com.phoenicis.tests;

import com.phoenicis.apps.ApplicationsSource;
import com.phoenicis.apps.dto.ApplicationDTO;
import com.phoenicis.apps.dto.CategoryDTO;
import com.phoenicis.apps.dto.ScriptDTO;
import com.phoenicis.multithreading.ControlledThreadPoolExecutorServiceCloser;
import com.phoenicis.scripts.interpreter.ScriptInterpreter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class PhoenicisTestsApp {
    private ApplicationContext applicationContext;
    private List<String> workingScripts = new ArrayList<>();
    private List<String> failingScripts = new ArrayList<>();

    public static void main(String[] args) {
        final PhoenicisTestsApp phoenicisTestsApp = new PhoenicisTestsApp();
        phoenicisTestsApp.run(args);
    }

    private void run(String[] args) {
        try(final ConfigurableApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(PhoenicisTestsConfiguration.class)) {

            final ApplicationsSource applicationsSource = applicationContext.getBean("mockedApplicationSource", ApplicationsSource.class);
            this.applicationContext = applicationContext;

            applicationsSource.fetchInstallableApplications(categoryDTOS -> {
                categoryDTOS.forEach(this::testCategory);
            }, e -> {
                throw new IllegalStateException(e);
            });

            applicationContext.getBean(ControlledThreadPoolExecutorServiceCloser.class).close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void testCategory(CategoryDTO categoryDTO) {
        System.out.println("+ " + categoryDTO.getName());
        categoryDTO.getApplications().forEach(applicationDTO -> this.testApplication(categoryDTO, applicationDTO));
    }

    private void testApplication(CategoryDTO categoryDTO, ApplicationDTO applicationDTO) {
        System.out.println("|-+ " + applicationDTO.getName());
        applicationDTO.getScripts().forEach(scriptDTO -> testScript(categoryDTO, applicationDTO, scriptDTO));
        System.out.println("|");
    }

    private void testScript(CategoryDTO categoryDTO, ApplicationDTO applicationDTO, ScriptDTO scriptDTO) {
        final ScriptInterpreter scriptInterpreter = applicationContext.getBean("nashornInterprpeter", ScriptInterpreter.class);
        System.out.print("| |-- " + scriptDTO.getName());
        try {
            scriptInterpreter.runScript(scriptDTO.getScript(), e -> {
                throw new TestException(e);
            });
            System.out.println(" [OK] ");
        } catch(TestException e) {
            e.printStackTrace();
            System.out.println(" [KO] ");
        }
    }
}
