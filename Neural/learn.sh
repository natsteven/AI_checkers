#!/bin/bash

# Define the number of iterations
iterations=$2
against=$1
if [[ $against != *"computer"* ]]; then
    against="other_players/$against"
fi
wins=0
loss=0
draw=0

for ((i = 1; i <= $iterations; i++)); do
    echo "Iteration $i:"
    
    # Run the checkers command and capture the last line of the output
    output=$(./checkers "java nHelper" $against 1 2>&1)
    
    # Check the content of the last line
    if [[ $output == *"Draw"* ]]; then
        echo "Draw"
        ((draw++))
        java learn 0.5
    elif [[ $output == *"Player 1 has lost"* ]]; then
        echo "Player 1 lost"
        if [[ $output == *"reached"* ]]; then
            echo "TIMEOUT"
        fi
        ((loss++))
        java learn 0
    elif [[ $output == *"Player 2 has lost"* ]]; then
        echo "Player 2 lost"
        ((wins++))
        java learn 1
    else
        echo "No match found in the output."
    fi
    echo "Wins: $wins/$i , Losses: $loss/$i , Draws: $draw/$i"
    echo "DATA, $wins, $loss, $draw"
    echo "-------------------------------------------------------"
done
