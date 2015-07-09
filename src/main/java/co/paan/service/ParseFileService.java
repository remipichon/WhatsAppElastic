package co.paan.service;

import co.paan.rest.DTO.ParseFileResponseDTO;

import java.io.File;

/**
 * Created by remi on 04/07/15.
 */
public interface ParseFileService {

    public ParseFileResponseDTO parseFile(String fileName, String conversationName);

}
