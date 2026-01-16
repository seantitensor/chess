/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */

package model;
import chess.ChessGame;

/**
 *
 * @author seantitensor
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

}
