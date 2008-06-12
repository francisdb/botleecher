/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher.service;

import eu.somatik.botleecher.model.PackList;
import java.io.File;

/**
 *
 * @author fdb
 */
public interface PackListReader {

    PackList readPacks(File listFile);

}
