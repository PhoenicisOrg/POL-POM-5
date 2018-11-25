module org.phoenicis.javafx {
    exports org.phoenicis.javafx;
    opens org.phoenicis.javafx;
    opens org.phoenicis.javafx.controller;
    opens org.phoenicis.javafx.settings;
    opens org.phoenicis.javafx.views;
    opens org.phoenicis.javafx.views.common;
    opens org.phoenicis.javafx.views.mainwindow.apps;
    opens org.phoenicis.javafx.views.mainwindow.console;
    opens org.phoenicis.javafx.views.mainwindow.containers;
    opens org.phoenicis.javafx.views.mainwindow.engines;
    opens org.phoenicis.javafx.views.mainwindow.installations;
    opens org.phoenicis.javafx.views.mainwindow.library;
    opens org.phoenicis.javafx.views.mainwindow.settings;
    opens org.phoenicis.javafx.views.scriptui;
    requires com.fasterxml.jackson.databind;
    requires commons.lang;
    requires fuzzywuzzy;
    requires java.desktop;
    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.web;
    requires jdk.scripting.nashorn;
    requires org.phoenicis.configuration;
    requires org.phoenicis.containers;
    requires org.phoenicis.engines;
    requires org.phoenicis.entities;
    requires org.phoenicis.library;
    requires org.phoenicis.multithreading;
    requires org.phoenicis.repository;
    requires org.phoenicis.scripts;
    requires org.phoenicis.settings;
    requires org.phoenicis.tools;
    requires org.phoenicis.win32;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
}
