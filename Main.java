import java.util.*; // importing all libraries

public class Main {
    private static final List<String> ranks = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
    private static final List<String> suits = Arrays.asList("♣", "♦", "♥", "♠");

    public static void main(String[] args) {
        Game poker = new Game();
        poker.play();
    }
}

class Game {
    private static final List<String> ranks = Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A");
    private static final List<String> suits = Arrays.asList("♣", "♦", "♥", "♠");

    private List<String> deck;
    private List<String> playerHand;
    private Random random;

    public Game() {
        random = new Random();
        deck = generateDeck();
        playerHand = dealHand();
    }

    public void play() {
    System.out.println("Welcome to the Poker Game!");

    int tokens = 10; // initial amount of tokens given

    while (tokens > 0) {
        System.out.println("\nTokens: " + tokens); // new line + telling amount of tokens
        System.out.println("P - Play a New Hand");
        System.out.println("Q - Quit Game");
        Scanner scanner = new Scanner(System.in); // getting input
        String choice = scanner.nextLine();

        if (choice.equalsIgnoreCase("q")) {  // quit game
            System.out.println("Thanks for playing!");
            break;
        } else if (choice.equalsIgnoreCase("p")) { // play game
            // Reset player hand and deck to stop hands from repeating
            playerHand.clear();
            deck = generateDeck();
            
            playerHand = dealHand(); // random 5 cards
            System.out.println("Your hand: ");
            showCards(playerHand); // showing the hand

            System.out.println("Enter the position of the cards you want to discard (1-5), or enter '0' to skip:");
            String[] discardIndices = scanner.nextLine().split(" ");

            List<String> discardedCards = new ArrayList<>(); // array list of indices that that user wants to discard
            for (String index : discardIndices) {
                int discardIndex = Integer.parseInt(index) - 1; // -1 because the indices range from 0-4, not 1-5
                if (discardIndex >= 0 && discardIndex < 5) {
                    discardedCards.add(playerHand.get(discardIndex));
                }
            }

            for (String card : discardedCards) {
                playerHand.remove(card); // discarding the cards
                deck.add(card); // adding the discarded card to the deck
            }

            if (discardedCards.size() > 0) {
                for (int i = 0; i < discardedCards.size(); i++) {
                    int randomIndex = random.nextInt(deck.size());
                    String newCard = deck.remove(randomIndex);
                    playerHand.add(newCard);
                }
            }

            System.out.println("Your new hand: ");
            showCards(playerHand);


            String handRanking = getHandRanking(playerHand);
            int tokensWon = calculateTokensWon(handRanking);

            System.out.println("Poker Hand: " + handRanking);
            System.out.println("Tokens Won: " + tokensWon);

            tokens += tokensWon;
        } else {
            System.out.println("Invalid choice! Please try again.");
        }
    }
}


    private List<String> generateDeck() {
        List<String> deck = new ArrayList<>();
        for (String rank : ranks) {
            for (String suit : suits) {
                deck.add(rank + " of " + suit);
            }
        }
        return deck;
    }

    private List<String> dealHand() {
        List<String> hand = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(deck.size());
            String card = deck.remove(randomIndex);
            hand.add(card);
        }
        return hand;
    }

    private void showCards(List<String> cards) {
        for (String card : cards) {
            int index = cards.indexOf(card);
            drawCard(card, index);
        }
    }

    private void drawCard(String card, int index) {
        String[] parts = card.split(" ");
        String rank = parts[0];
        String suit = parts[2];

        System.out.println(".------.");
        if (rank.equals("10")) { // spacing messed up because 10 is two characters
            System.out.println("|" + rank + "    |");
        } else {
            System.out.println("|" + rank + "     |");
        }
        System.out.println("|      |");
        System.out.println("|  " + suit.charAt(0) + "   |" + "  ------  " + String.valueOf(index+1)); 
        System.out.println("|      |");
        if (rank.equals("10")) { // spacing messed up because 10 is two characters
            System.out.println("|    " + rank + "|");
        } else {
            System.out.println("|     " + rank + "|");
        }

        System.out.println("'------'");
    }

    private String getHandRanking(List<String> hand) {
        if (isRoyalFlush(hand)) {
            return "Royal Flush";
        } else if (isStraightFlush(hand)) {
            return "Straight Flush";
        } else if (isFourOfAKind(hand)) {
            return "Four of a Kind";
        } else if (isFullHouse(hand)) {
            return "Full House";
        } else if (isFlush(hand)) {
            return "Flush";
        } else if (isStraight(hand)) {
            return "Straight";
        } else if (isThreeOfAKind(hand)) {
            return "Three of a Kind";
        } else if (isTwoPair(hand)) {
            return "Two Pair";
        } else if (isOnePair(hand)) {
            return "One Pair";
        } else {
            return "High Card (Tokens Lost!)";
        }
    }

    private boolean isRoyalFlush(List<String> hand) {
        // Check if the hand has A, K, Q, J, 10 of the same suit
        List<String> ranksToCheck = Arrays.asList("A", "K", "Q", "J", "10");

        for (String suit : suits) {
            boolean hasAllRanks = true;
            for (String rank : ranksToCheck) {
                if (!hand.contains(rank + " of " + suit)) {
                    hasAllRanks = false;
                    break;
                }
            }
            if (hasAllRanks) {
                return true;
            }
        }

        return false;
    }

    private boolean isStraightFlush(List<String> hand) {
        // Check if the hand has any five consecutive cards of the same suit
        for (String suit : suits) {
            List<String> sameSuitCards = new ArrayList<>();
            for (String card : hand) {
                if (card.endsWith(suit)) {
                    sameSuitCards.add(card);
                }
            }
            if (sameSuitCards.size() >= 5) {
                List<String> sortedRanks = sortRanks(sameSuitCards);
                if (isConsecutiveRanks(sortedRanks)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFourOfAKind(List<String> hand) {
        // Check if the hand has four cards of the same rank
        for (String rank : ranks) {
            int count = 0;
            for (String card : hand) {
                if (card.startsWith(rank)) {
                    count++;
                }
            }
            if (count >= 4) {
                return true;
            }
        }
        return false;
    }

    private boolean isFullHouse(List<String> hand) {
        // Check if the hand has three cards of one rank and two cards of another rank
        boolean hasThreeOfAKind = false;
        boolean hasPair = false;

        for (String rank : ranks) {
            int count = 0;
            for (String card : hand) {
                if (card.startsWith(rank)) {
                    count++;
                }
            }
            if (count == 3) {
                hasThreeOfAKind = true;
            } else if (count == 2) {
                hasPair = true;
            }
        }

        return hasThreeOfAKind && hasPair;
    }

    private boolean isFlush(List<String> hand) {
        // Check if the hand has any five cards of the same suit, not in consecutive order
        for (String suit : suits) {
            int count = 0;
            for (String card : hand) {
                if (card.endsWith(suit)) {
                    count++;
                }
            }
            if (count >= 5) {
                return true;
            }
        }
        return false;
    }

    private boolean isStraight(List<String> hand) {
        // Check if the hand has any five consecutive cards of mixed suits
        List<String> sortedRanks = sortRanks(hand);
        return isConsecutiveRanks(sortedRanks);
    }

    private boolean isThreeOfAKind(List<String> hand) {
        // Check if the hand has three cards of the same rank
        for (String rank : ranks) {
            int count = 0;
            for (String card : hand) {
                if (card.startsWith(rank)) {
                    count++;
                }
            }
            if (count >= 3) {
                return true;
            }
        }
        return false;
    }

    private boolean isTwoPair(List<String> hand) {
        // Check if the hand has two sets of two cards of the same rank
        int pairCount = 0;

        for (String rank : ranks) {
            int count = 0;
            for (String card : hand) {
                if (card.startsWith(rank)) {
                    count++;
                }
            }
            if (count == 2) {
                pairCount++;
            }
        }

        return pairCount >= 2;
    }

    private boolean isOnePair(List<String> hand) {
        // Check if the hand has two cards of the same rank
        for (String rank : ranks) {
            int count = 0;
            for (String card : hand) {
                if (card.startsWith(rank)) {
                    count++;
                }
            }
            if (count >= 2) {
                return true;
            }
        }
        return false;
    }

    private List<String> sortRanks(List<String> hand) {
        List<String> sortedRanks = new ArrayList<>(hand);
        sortedRanks.sort(Comparator.comparingInt(ranks::indexOf));
        return sortedRanks;
    }

    private boolean isConsecutiveRanks(List<String> ranks) {
        for (int i = 1; i < ranks.size(); i++) {
            int currentRankIndex = this.ranks.indexOf(ranks.get(i));
            int previousRankIndex = this.ranks.indexOf(ranks.get(i - 1));
            if (currentRankIndex != previousRankIndex + 1) {
                return false;
            }
        }
        return true;
    }

    private int calculateTokensWon(String handRanking) {
        switch (handRanking) {
            case "Royal Flush":
                return 250;
            case "Straight Flush":
                return 50;
            case "Four of a Kind":
                return 25;
            case "Full House":
                return 9;
            case "Flush":
                return 6;
            case "Straight":
                return 4;
            case "Three of a Kind":
                return 3;
            case "Two Pair":
                return 2;
            case "One Pair":
                return 1;
            default:
                return -2;
        }
    }
}
