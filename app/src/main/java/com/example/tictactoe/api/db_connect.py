# db_connect.py
from flask import Flask, request, jsonify
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)

def create_connection():
    try:
        connection = mysql.connector.connect(
            host='localhost',
            user='root',
            password='',
            database='tic_tac_toe'
        )
        if connection.is_connected():
            print("Database connected")  # Debugging
            return connection
    except Error as e:
        print(f"Error: {e}")
        return None

def close_connection(connection):
    """Close the MySQL database connection."""
    if connection.is_connected():
        connection.close()

@app.route('/insert_game', methods=['POST'])
def insert_game():
    data = request.json
    try:
        connection = create_connection()
        if not connection:
            return jsonify({'error': 'Failed to connect to the database'}), 500

        cursor = connection.cursor()
        cursor.execute("INSERT INTO games (size) VALUES (%s)", (data['size'],))
        connection.commit()
        game_id = cursor.lastrowid  # Get the last inserted ID
        close_connection(connection)
        return jsonify({"id": game_id}), 201
    except Error as e:
        return jsonify({'error': str(e)}), 500

@app.route('/insert_move', methods=['POST'])
def insert_move():
    data = request.json
    game_id = data.get('game_id')
    player = data.get('player')
    row = data.get('row')
    col = data.get('col')

    if not all([game_id, player, row is not None, col is not None]):
        return jsonify({'error': 'Missing data'}), 400

    try:
        connection = create_connection()
        if not connection:
            return jsonify({'error': 'Failed to connect to the database'}), 500

        cursor = connection.cursor()
        cursor.execute(
            "INSERT INTO moves (game_id, player, row, col) VALUES (%s, %s, %s, %s)",
            (game_id, player, row, col)
        )
        connection.commit()

        # Check if the game should be marked as completed
        if should_end_game(connection, game_id):
            update_game_status(connection, game_id)

        close_connection(connection)
        return jsonify({'message': 'Move inserted'}), 201
    except Error as e:
        return jsonify({'error': str(e)}), 500

@app.route('/get_moves/<int:game_id>', methods=['GET'])
def get_moves(game_id):
    """Retrieve all moves for a given game."""
    try:
        connection = create_connection()
        if not connection:
            return jsonify({'error': 'Failed to connect to the database'}), 500

        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT * FROM moves WHERE game_id = %s", (game_id,))
        moves = cursor.fetchall()
        close_connection(connection)
        return jsonify(moves), 200
    except Error as e:
        return jsonify({'error': str(e)}), 500

@app.route('/get_games', methods=['GET'])
def get_games():
    """Retrieve all games."""
    try:
        connection = create_connection()
        if not connection:
            return jsonify({'error': 'Failed to connect to the database'}), 500

        cursor = connection.cursor(dictionary=True)
        cursor.execute("SELECT * FROM games")
        games = cursor.fetchall()
        close_connection(connection)
        return jsonify(games), 200
    except Error as e:
        return jsonify({'error': str(e)}), 500

@app.route('/update_game', methods=['POST'])
def update_game():
    data = request.json
    game_id = data.get('game_id')
    status = data.get('status')
    winner = data.get('winner')

    if not all([game_id, status]):
        return jsonify({'error': 'Missing data'}), 400

    if status not in ['ONGOING', 'COMPLETED']:
        return jsonify({'error': 'Invalid status'}), 400

    if status == 'COMPLETED' and winner not in ['X', 'O', 'DRAW']:
        return jsonify({'error': 'Invalid winner'}), 400

    try:
        connection = create_connection()
        if not connection:
            return jsonify({'error': 'Failed to connect to the database'}), 500

        cursor = connection.cursor()
        cursor.execute(
            "UPDATE games SET status = %s, winner = %s WHERE id = %s",
            (status, winner, game_id)
        )
        connection.commit()
        close_connection(connection)
        return jsonify({'message': 'Game updated'}), 200
    except Error as e:
        return jsonify({'error': str(e)}), 500

def should_end_game(connection, game_id):
    """Check if the game should be marked as completed (dummy implementation)."""
    # Implement your logic to check if the game has ended (win/draw/full board)
    return False

def update_game_status(connection, game_id):
    """Update the game status based on game logic (dummy implementation)."""
    # Implement your logic to determine the winner and update the game status
    pass

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
