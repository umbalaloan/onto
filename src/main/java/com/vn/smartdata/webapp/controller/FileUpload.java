package com.vn.smartdata.webapp.controller;



/**
 * Command class to handle uploading of a file.
 *
 * <p>
 * <a href="FileUpload.java.html"><i>View Source</i></a>
 * </p>
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
public class FileUpload {
    
    /** The name. */
    private String name;
    
    /** The file. */
    private byte[] file;

    /**
     * Gets the name.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the file.
     *
     * @param file the new file
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * Gets the file.
     *
     * @return the file
     */
    public byte[] getFile() {
        return file;
    }
}
