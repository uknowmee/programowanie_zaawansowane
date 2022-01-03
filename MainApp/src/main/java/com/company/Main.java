package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println(Reader.imReader());
        System.out.println(Writer.imWriter());

        Scanner scanner = new Scanner(System.in);

        int number = scanner.nextInt();
        System.out.println("your number is: " + number);

        number = multiByTwo(number);

        System.out.println("your number multiplied by 2 is: " + number);
    }

    public static int multiByTwo(int number) {
        return number * 2;
    }
}
