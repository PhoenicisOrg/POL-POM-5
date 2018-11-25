module org.phoenicis.tools {
    exports org.phoenicis.tools;
    exports org.phoenicis.tools.files;
    exports org.phoenicis.tools.config;
    exports org.phoenicis.tools.system;
    exports org.phoenicis.tools.system.opener;
    exports org.phoenicis.tools.version;
    opens org.phoenicis.tools;
    opens org.phoenicis.tools.files;
    opens org.phoenicis.tools.system;
    requires bcpg.jdk16;
    requires bcprov.jdk16;
    requires java.activation;
    requires jmimemagic;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.google.common;
    requires commons.lang;
    requires org.apache.commons.codec;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.phoenicis.configuration;
    requires org.phoenicis.entities;
    requires org.phoenicis.win32;
    requires slf4j.api;
    requires spring.beans;
    requires spring.context;
}
