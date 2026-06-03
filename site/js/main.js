(function () {
    // Remplace le / du vide
    const API = window.APP_CONFIG.API_BASE_URL.replace(/\/$/, '');

    // Creer la map
    const map = L.map('map').setView([48.692054, 6.184417], 13);
    //Modifie le fond
    L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {maxZoom: 19, attribution: '&copy; OpenStreetMap contributors &copy; CARTO'}).addTo(map);

    // Define a function to create a coloured circle marker
    function createCircleMarker(lat, lon, colour) {
        return L.circleMarker([lat, lon], {
            radius: 7,
            fillColor: colour,
            color: colour,
            weight: 1,
            opacity: 1,
            fillOpacity: 0.8
        });
    }

    // Load restaurants and add them to the map
    function loadRestaurants() {
        fetch(`${API}/api/restaurants`)
            .then(response => response.json())
            .then(restaurants => {
                restaurants.forEach(r => {
                    const marker = createCircleMarker(r.latitude, r.longitude, 'blue');
                    const popupContent = `<strong>${r.nom}</strong><br>` +
                        `${r.adresse}<br>` +
                        `<button onclick="reserveRestaurant('${r.id}', '${r.nom.replace(/'/g, "\'")}')">Réserver</button>`;
                    marker.bindPopup(popupContent);
                    marker.addTo(map);
                });
            })
            .catch(err => console.error('Erreur lors du chargement des restaurants:', err));
    }

    // Load bicycle stations and add them to the map
    function loadVelos() {
        fetch(`${API}/api/velos`)
            .then(response => response.json())
            .then(stations => {
                stations.forEach(s => {
                    const marker = createCircleMarker(s.lat, s.lon, 'green');
                    const popupContent = `<strong>${s.name}</strong><br>` +
                        `${s.adresse || ''}<br>` +
                        `Vélos disponibles : ${s.numBikesAvailable}<br>` +
                        `Places libres : ${s.numDocksAvailable}`;
                    marker.bindPopup(popupContent);
                    marker.addTo(map);
                });
            })
            .catch(err => console.error('Erreur lors du chargement des stations vélo:', err));
    }

    // Load incidents and add them to the map
    function loadIncidents() {
        fetch(`${API}/api/incidents`)
            .then(response => response.json())
            .then(incidents => {
                incidents.forEach(i => {
                    const marker = createCircleMarker(i.lat, i.lon, 'red');
                    const popupContent = `<strong>Incident</strong><br>` +
                        `${i.adresse || ''}<br>` +
                        `Cause : ${i.cause || 'N/A'}<br>` +
                        `Du ${i.start || '?'} au ${i.end || '?'}`;
                    marker.bindPopup(popupContent);
                    marker.addTo(map);
                });
            })
            .catch(err => console.error('Erreur lors du chargement des incidents:', err));
    }

    // Expose a global reservation function used by popups
    window.reserveRestaurant = function (restaurantId, restaurantName) {
        const nom = prompt('Nom de famille pour la réservation :');
        if (!nom) return;
        const prenom = prompt('Prénom :');
        if (!prenom) return;
        const nb = prompt('Nombre de convives :', '2');
        const nbConvives = parseInt(nb, 10) || 1;
        const telephone = prompt('Téléphone :');
        if (!telephone) return;
        const payload = {
            restaurantId: restaurantId,
            nom: nom,
            prenom: prenom,
            nbConvives: nbConvives,
            telephone: telephone
        };
        fetch(`${API}/api/reservations`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(res => res.json())
            .then(result => {
                if (result.success) {
                    alert(`Réservation réussie pour ${restaurantName}`);
                } else {
                    alert(`Échec de la réservation : ${result.error || result.message}`);
                }
            })
            .catch(err => alert('Erreur lors de la réservation : ' + err));
    };

    // Kick off data loading when the page is ready
    window.addEventListener('load', () => {
        loadRestaurants();
        loadVelos();
        loadIncidents();
    });
})();