package com.jamesansley;

import com.jamesansley.game.Join4;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Join4 game = new Join4(1);
        Scanner s = new Scanner(System.in);

        System.out.println(game);
        while (!game.isWin()) {
            System.out.print("> ");
            int move = s.nextInt();
            game = game.move(move);
            System.out.println(game);
        }
    }
}
