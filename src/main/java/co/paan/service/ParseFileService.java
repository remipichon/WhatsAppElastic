package co.paan.service;

import co.paan.rest.DTO.ParseFileResponseDTO;

import java.io.File;
import java.io.InputStream;

/**
 * Created by remi on 04/07/15.
 */
public interface ParseFileService {


    ParseFileResponseDTO parseFile(InputStream inputStream, String fileName, String conversationName);
}
