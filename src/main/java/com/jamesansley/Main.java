package com.jamesansley;

import com.jamesansley.game.Join4;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Join4 game = new Join4(1);
        Scanner s = new Scanner(System.in);

        System.out.println(game);
        System.out.print("> ");
        int move = s.nextInt();
        while (0 < move && move <= 7) {
            game = game.move(move);
            System.out.println(game);
            System.out.print("> ");
            move = s.nextInt();
        }
    }
}
