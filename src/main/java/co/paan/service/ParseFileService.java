package co.paan.service;

import java.io.File;

/**
 * Created by remi on 04/07/15.
 */
public interface ParseFileService {

    public Boolean parseFile(String filename);

    public Boolean parseFile(File file);
}
