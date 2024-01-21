# EECE430L_Backend

This is the backend for an exchange rate application, built with Flask. It provides various APIs for creating and managing transactions, exchange rates, and users.

# Getting Started
Clone the repository using the following link:

https://github.com/MounirKhalil/EECE430L_Backend


# Prerequisites
To set up the environment, follow these steps:
**Step 1:** Activate the virtual environment:
venv\Scripts\activate

**Step 2:** Install the required packages from the requirements.txt file:
pip install -r requirements.txt

# Running the application
**Step 3:** Run the Flask application with the following command:
flask --app app.py --debug run

# API Endpoints
The backend provides the following API endpoints:

/transaction (POST): Create a new transaction
/addrequest (POST): Add a new exchange request
/transaction (GET): Get all transactions of the current user
/getuserrequests (GET): Get all exchange requests of the current user
/getotherrequests (GET): Get all exchange requests from other users
/getaccepteduserrequests (GET): Get all accepted exchange requests of the current user
/getacceptedotherrequests (GET): Get all accepted exchange requests from other users
/acceptexchange (PUT): Accept an exchange request
/exchangeRate (GET): Get the exchange rate
/graph_sell (GET): Get the graph data for selling exchange rates
/graph_buy (GET): Get the graph data for buying exchange rates
/graph_overallsell (GET): Get the overall graph data for selling exchange rates
/graph_overallbuy (GET): Get the overall graph data for buying exchange rates
/user (POST): Create a new user
/authentication (POST): Authenticate a user
