

import React, { useState, useEffect, useRef } from "react";
import "./App.css";

function App() {

  // =========================
  // AUTH STATE
  // =========================
const [swapSuccessMessage, setSwapSuccessMessage] = useState("");
const swapSuccessRef = useRef(null);

useEffect(() => {

  if (
    swapSuccessMessage &&
    swapSuccessRef.current
  ) {

    swapSuccessRef.current.scrollIntoView({
      behavior: "smooth",
      block: "center"
    });

  }

}, [swapSuccessMessage]);
  const [isLoggedIn, setIsLoggedIn] = useState(
    !!localStorage.getItem("token")
  );

  const [authMode, setAuthMode] = useState("login");

  const [authForm, setAuthForm] = useState({
    name: "",
    email: "",
    password: ""
  });


  // =========================
  // SEATSWAP STATE
  // =========================

  const [pnr, setPnr] = useState("");
  const [bookings, setBookings] = useState([]);

  const [outgoingRequests, setOutgoingRequests] =
    useState([]);

  const [incomingRequests, setIncomingRequests] =
    useState([]);

  const [selectedBooking, setSelectedBooking] =
    useState(null);

  const [swapOptions, setSwapOptions] =
    useState([]);


  // =========================
  // AUTHENTICATION
  // =========================

  const handleAuth = async (event) => {

    event.preventDefault();

    const endpoint =
      authMode === "login"
        ? "/api/auth/login"
        : "/api/auth/signup";

    try {

      const response = await fetch(
        `http://localhost:8080${endpoint}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify(authForm)
        }
      );

      const text = await response.text();

      let data = {};

      if (text) {
        data = JSON.parse(text);
      }

      if (!response.ok) {
        throw new Error(
          data.message || "Authentication failed"
        );
      }


      // =========================
      // LOGIN
      // =========================

      if (authMode === "login") {

        localStorage.setItem(
          "token",
          data.token
        );

        setIsLoggedIn(true);

        setAuthForm({
          name: "",
          email: "",
          password: ""
        });

        alert("Login successful!");

        // Load incoming requests immediately
        await loadIncomingRequests();

      }


      // =========================
      // SIGNUP
      // =========================

      else {

        alert(
          "Signup successful! Please login."
        );

        setAuthMode("login");

        setAuthForm({
          name: "",
          email: authForm.email,
          password: ""
        });

      }

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // LOGOUT
  // =========================

  const handleLogout = () => {

    localStorage.removeItem("token");

    setIsLoggedIn(false);

    setAuthMode("login");
    setSwapSuccessMessage("");

    setAuthForm({
      name: "",
      email: "",
      password: ""
    });

    setPnr("");

    setBookings([]);

    setSelectedBooking(null);

    setSwapOptions([]);

    setIncomingRequests([]);

    setOutgoingRequests([]);
  };


  // =========================
  // SEARCH BOOKING
  // =========================

  const searchBooking = async () => {

    if (!pnr) {
      alert("Please enter your PNR");
      return;
    }

    try {

      const token =
        localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/bookings/${pnr}`,
        {
          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );

      const data = await response.json();

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Booking not found"
        );

      }

      setBookings(data);

      setSelectedBooking(null);

      setSwapOptions([]);

      setOutgoingRequests([]);

      // Do NOT clear incoming requests here.
      // Incoming requests belong to the
      // authenticated passenger.

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // SELECT BOOKING
  // =========================

  const selectBooking = async (booking) => {

    setSelectedBooking(booking);

    setSwapOptions([]);

    setOutgoingRequests([]);

    try {

      const token =
        localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/swap-requests/options/${pnr}?bookingId=${booking.bookingId}`,
        {
          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );

      const data = await response.json();

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Unable to load swap options"
        );

      }

      setSwapOptions(data);

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // CREATE SWAP REQUEST
  // =========================

  const requestSwap = async (seatId) => {

    if (!selectedBooking) {

      alert(
        "Please select a passenger first"
      );

      return;
    }

    if (!seatId) {

      alert(
        "Please select a seat"
      );

      return;
    }

    try {

      const token =
        localStorage.getItem("token");

      const response = await fetch(
        "http://localhost:8080/api/swap-requests",
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json",

            Authorization:
              `Bearer ${token}`
          },

          body: JSON.stringify({

            pnr: pnr,

            bookingId:
              selectedBooking.bookingId,

            requestedSeatId:
              seatId

          })
        }
      );

      const data =
        await response.json();

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Unable to create swap request"
        );

      }

      alert(
        "Swap request created successfully!"
      );

      console.log(data);

      await loadOutgoingRequests();

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // LOAD INCOMING REQUESTS
  // =========================

  const loadIncomingRequests = async () => {

    try {

      const token =
        localStorage.getItem("token");

      if (!token) {
        return;
      }

      const response = await fetch(
        "http://localhost:8080/api/swap-requests/incoming",
        {
          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );

      const data =
        await response.json();

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Unable to load incoming swap requests"
        );

      }

      setIncomingRequests(data);

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // LOAD OUTGOING REQUESTS
  // =========================

  const loadOutgoingRequests = async () => {

    if (!selectedBooking) {

      alert(
        "Please select a passenger first"
      );

      return;
    }

    try {

      const token =
        localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/swap-requests/outgoing/${selectedBooking.bookingId}`,
        {
          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );

      const data =
        await response.json();

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Unable to load outgoing requests"
        );

      }

      setOutgoingRequests(data);

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // CANCEL SWAP
  // =========================

  const cancelSwap = async (swapRequestId) => {

    if (!selectedBooking) {

      alert(
        "Please select a passenger first"
      );

      return;
    }

    try {

      const token =
        localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/swap-requests/${swapRequestId}/cancel?bookingId=${selectedBooking.bookingId}`,
        {
          method: "POST",

          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );

      const data =
        await response.json();

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Unable to cancel swap request"
        );

      }

      alert(
        "Swap request cancelled successfully!"
      );

      await loadOutgoingRequests();

    } catch (error) {

      alert(error.message);

    }
  };


  // =========================
  // ACCEPT SWAP
  // =========================


  const acceptSwap = async (swapRequestId) => {

    try {

      const token =
        localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/swap-requests/${swapRequestId}/accept`,
        {
          method: "POST",

          headers: {
            Authorization:
              `Bearer ${token}`
          }
        }
      );

      const text = await response.text();

      let data = {};

      if (text) {
        data = JSON.parse(text);
      }

      if (!response.ok) {

        throw new Error(
          data.message ||
          "Unable to accept swap"
        );

      }

      // =========================
      // SWAP SUCCESS
      // =========================

     setSwapSuccessMessage(
       "Seat swap completed successfully! Your seat has been exchanged."
     );

      console.log(data);


      // =========================
      // REFRESH INCOMING REQUESTS
      // =========================

      await loadIncomingRequests();


      // =========================
      // REFRESH BOOKING ONLY IF
      // USER HAS SEARCHED A PNR
      // =========================

      if (pnr) {

        const bookingResponse =
          await fetch(
            `http://localhost:8080/api/bookings/${pnr}`,
            {
              headers: {
                Authorization:
                  `Bearer ${token}`
              }
            }
          );

        const bookingText =
          await bookingResponse.text();

        let updatedBookings = [];

        if (bookingText) {
          updatedBookings =
            JSON.parse(bookingText);
        }

        if (!bookingResponse.ok) {

          throw new Error(
            updatedBookings.message ||
            "Unable to refresh booking"
          );

        }

        setBookings(updatedBookings);


        // =========================
        // UPDATE SELECTED BOOKING
        // =========================

        if (selectedBooking) {

          const updatedSelectedBooking =
            updatedBookings.find(
              (booking) =>
                booking.bookingId ===
                selectedBooking.bookingId
            );

          if (updatedSelectedBooking) {

            setSelectedBooking(
              updatedSelectedBooking
            );


            // =========================
            // REFRESH SWAP OPTIONS
            // =========================

            const optionsResponse =
              await fetch(
                `http://localhost:8080/api/swap-requests/options/${pnr}?bookingId=${updatedSelectedBooking.bookingId}`,
                {
                  headers: {
                    Authorization:
                      `Bearer ${token}`
                  }
                }
              );

            const optionsText =
              await optionsResponse.text();

            let updatedOptions = [];

            if (optionsText) {
              updatedOptions =
                JSON.parse(optionsText);
            }

            if (optionsResponse.ok) {

              setSwapOptions(
                updatedOptions
              );

            }

          }

        }


        // =========================
        // REFRESH OUTGOING REQUESTS
        // =========================

        if (selectedBooking) {

          await loadOutgoingRequests();

        }

      }

    } catch (error) {

      alert(error.message);

    }
  };




  // =========================================================
  // LOGIN / SIGNUP SCREEN
  // =========================================================

  if (!isLoggedIn) {

    return (

      <div className="auth-container">

        <div className="logo">
          Seat<span>Swap</span>
        </div>


        <h1>

          {authMode === "login"
            ? "Welcome Back"
            : "Create Account"}

        </h1>


        <p className="subtitle">

          {authMode === "login"
            ? "Login to manage your seat swaps."
            : "Create your SeatSwap passenger account."}

        </p>


        <form
          onSubmit={handleAuth}
        >

          {authMode === "signup" && (

            <input
              type="text"
              placeholder="Name"
              value={authForm.name}

              onChange={(e) =>
                setAuthForm({
                  ...authForm,
                  name: e.target.value
                })
              }

              required
            />

          )}


          <input
            type="email"
            placeholder="Email"
            value={authForm.email}

            onChange={(e) =>
              setAuthForm({
                ...authForm,
                email: e.target.value
              })
            }

            required
          />


          <input
            type="password"
            placeholder="Password"
            value={authForm.password}

            onChange={(e) =>
              setAuthForm({
                ...authForm,
                password: e.target.value
              })
            }

            required
          />


          <button type="submit">

            {authMode === "login"
              ? "Login"
              : "Sign Up"}

          </button>

        </form>


        <div className="auth-switch">

          {authMode === "login"
            ? "Don't have an account?"
            : "Already have an account?"}


          <button
            type="button"

            onClick={() =>
              setAuthMode(
                authMode === "login"
                  ? "signup"
                  : "login"
              )
            }
          >

            {authMode === "login"
              ? "Sign Up"
              : "Login"}

          </button>

        </div>

      </div>

    );

  }


  // =========================================================
  // MAIN SEATSWAP APPLICATION
  // =========================================================

  return (

    <div className="app">

      {/* =========================
          NAVBAR
          ========================= */}

      <header className="navbar">

        <div className="logo">
          Seat<span>Swap</span>
        </div>


        <div className="nav-user">

          Passenger Portal


          <button
            className="logout-button"
            onClick={handleLogout}
          >

            Logout

          </button>

        </div>

      </header>


      <main className="container">

{swapSuccessMessage && (
  <div
    ref={swapSuccessRef}
    className="success-message"
  >
    {swapSuccessMessage}
  </div>

)}

        {/* =========================
            HERO SECTION
            ========================= */}

        <section className="hero">

          <h1>

            Swap your seat,
            <br />

            <span>
              travel comfortably.
            </span>

          </h1>


          <p>

            Find another passenger willing to exchange
            seats and complete the swap securely.

          </p>

        </section>


        {/* =========================
            BOOKING SEARCH
            ========================= */}

        <section className="booking-card">

          <h2>
            Find your booking
          </h2>


          <p className="subtitle">

            Enter your 10-digit PNR to view your current
            seat.

          </p>


          <div className="search-box">

            <input
              type="text"
              placeholder="Enter PNR"
              value={pnr}

              onChange={(e) =>
                setPnr(e.target.value)
              }

              maxLength="10"
            />


            <button
              onClick={searchBooking}
            >

              Search Booking

            </button>

          </div>

        </section>


        {/* =========================
            INCOMING REQUESTS
            ========================= */}

        <section className="booking-result">

          <h2>
            Incoming Swap Requests
          </h2>


          <button
            className="incoming-button"
            onClick={loadIncomingRequests}
          >

            View Incoming Swap Requests

          </button>


          {incomingRequests.length === 0 ? (

            <p className="subtitle">

              No pending incoming requests.

            </p>

          ) : (

            incomingRequests.map(
              (request) => (

                <div
                  className="request-card"
                  key={request.id}
                >

                  <div>

                    <span className="label">
                      REQUEST FROM
                    </span>


                    <strong>
                      {request.requesterName}
                    </strong>

                  </div>


                  <div>

                    <span className="label">
                      CURRENT SEAT
                    </span>


                    <strong>

                      {request.currentCoach}
                      {" · "}
                      {request.currentSeat}

                    </strong>

                  </div>


                  <div>

                    <span className="label">
                      REQUESTED SEAT
                    </span>


                    <strong>

                      {request.requestedCoach}
                      {" · "}
                      {request.requestedSeat}

                    </strong>

                  </div>


                  <div>

                    <span className="status">
                      {request.status}
                    </span>


                    <button
                      className="accept-button"

                      onClick={() =>
                        acceptSwap(
                          request.id
                        )
                      }
                    >

                      Accept Swap

                    </button>

                  </div>

                </div>

              )
            )

          )}

        </section>


        {/* =========================
            BOOKING RESULTS
            ========================= */}

        {bookings.length > 0 && (

          <section className="booking-result">


            {/* RESULT HEADER */}

            <div className="result-header">

              <div>

                <span className="label">
                  PNR
                </span>


                <h2>
                  {pnr}
                </h2>

              </div>


              <span className="status">

                {bookings.length}
                {" "}
                PASSENGER
                {bookings.length > 1
                  ? "S"
                  : ""}

              </span>

            </div>


            {/* PASSENGER LIST */}

            <div className="passenger-list">

              {bookings.map(
                (booking) => (

                  <div
                    className="seat-option"
                    key={booking.bookingId}
                  >

                    <div>

                      <span className="label">
                        PASSENGER
                      </span>


                      <strong>
                        {booking.passengerName}
                      </strong>

                    </div>


                    <div>

                      <span className="label">
                        COACH
                      </span>


                      <strong>
                        {booking.coachNumber}
                      </strong>

                    </div>


                    <div>

                      <span className="label">
                        CURRENT SEAT
                      </span>


                      <strong>
                        {booking.seatNumber}
                      </strong>

                    </div>


                    <div>

                      <span className="label">
                        BERTH
                      </span>


                      <strong>
                        {booking.berthType}
                      </strong>

                    </div>


                    <button
                      className="swap-button"

                      onClick={() =>
                        selectBooking(
                          booking
                        )
                      }
                    >

                      {selectedBooking?.bookingId ===
                      booking.bookingId

                        ? "Selected"

                        : "Request Seat Swap"}

                    </button>

                  </div>

                )
              )}

            </div>


            {/* =========================
                SELECTED PASSENGER
                ========================= */}

            {selectedBooking && (

              <div className="swap-section">

                <h3>

                  Swap seat for{" "}

                  {selectedBooking.passengerName}

                </h3>


                <p className="subtitle">

                  Current seat:{" "}

                  <strong>

                    {selectedBooking.coachNumber}

                    {" · "}

                    {selectedBooking.seatNumber}

                    {" "}

                    {selectedBooking.berthType}

                  </strong>

                </p>


                <h3>
                  Choose a seat to swap
                </h3>


                {swapOptions.length === 0 ? (

                  <p>
                    No swap options available.
                  </p>

                ) : (

                  swapOptions.map(
                    (option) => (

                      <div
                        className="seat-option"
                        key={option.seatId}
                      >

                        <div>

                          <span className="label">
                            SEAT
                          </span>


                          <strong>

                            {option.coachNumber}
                            {" · "}
                            {option.seatNumber}

                          </strong>

                        </div>


                        <div>

                          <span className="label">
                            BERTH
                          </span>


                          <strong>
                            {option.berthType}
                          </strong>

                        </div>


                        <div>

                          <span className="label">
                            PASSENGER
                          </span>


                          <strong>
                            {option.passengerName}
                          </strong>

                        </div>


                        <button
                          className="swap-button"

                          onClick={() =>
                            requestSwap(
                              option.seatId
                            )
                          }
                        >

                          Request Swap with{" "}

                          {option.passengerName}

                        </button>

                      </div>

                    )
                  )

                )}

              </div>

            )}

          </section>

        )}


        {/* =========================
            OUTGOING REQUESTS
            ========================= */}

        {selectedBooking && (

          <section className="booking-result">

            <h2>
              My Swap Requests
            </h2>


            <button
              className="incoming-button"
              onClick={loadOutgoingRequests}
            >

              View My Swap Requests

            </button>


            {outgoingRequests.length === 0 ? (

              <p className="subtitle">

                No pending swap requests.

              </p>

            ) : (

              outgoingRequests.map(
                (request) => (

                  <div
                    className="request-card"
                    key={request.id}
                  >

                    <div>

                      <span className="label">
                        REQUEST TO SEAT
                      </span>


                      <strong>

                        {request.requestedCoach}
                        {" · "}
                        {request.requestedSeat}

                      </strong>

                    </div>


                    <div>

                      <span className="label">
                        MY CURRENT SEAT
                      </span>


                      <strong>

                        {request.currentCoach}
                        {" · "}
                        {request.currentSeat}

                      </strong>

                    </div>


                    <div>

                      <span className="label">
                        STATUS
                      </span>


                      <span className="status">

                        {request.status}

                      </span>

                    </div>


                    <div>

                      <button
                        className="accept-button"

                        onClick={() =>
                          cancelSwap(
                            request.id
                          )
                        }
                      >

                        Cancel Request

                      </button>

                    </div>

                  </div>

                )
              )

            )}

          </section>

        )}

      </main>

    </div>

  );

}

export default App;

