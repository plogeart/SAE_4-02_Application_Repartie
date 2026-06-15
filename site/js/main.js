const API = window.APP_CONFIG.API_BASE_URL;

let restaurants = [];
let tables = [];

const map = L.map('map').setView([48.692054, 6.184417], 13);

L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    maxZoom: 19,
    attribution: 'OpenStreetMap / CARTO'
}).addTo(map);

const restaurantLayer = L.layerGroup().addTo(map);
const veloLayer = L.layerGroup().addTo(map);
const incidentLayer = L.layerGroup().addTo(map);

function ajouterPoint(layer, lat, lon, couleur, texte) {
    L.circleMarker([lat, lon], {
        radius: 8,
        color: couleur,
        fillColor: couleur,
        fillOpacity: 0.8
    }).bindPopup(texte).addTo(layer);
}

function getJson(route) {
    return fetch(API + route).then(function(response) {
        return response.json();
    });
}

function postJson(route, donnees) {
    return fetch(API + route, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(donnees)
    }).then(function(response) {
        return response.json();
    });
}

function loadRestaurants() {
    getJson('/api/restaurants').then(function(data) {
        restaurants = data;
        restaurantLayer.clearLayers();

        restaurants.forEach(function(r) {
            let popup = '<b>' + r.nom + '</b><br>' +
                r.adresse + '<br><br>' +
                '<button onclick="reserver(' + r.id + ')">Reserver</button>';

            ajouterPoint(restaurantLayer, r.latitude, r.longitude, 'blue', popup);
        });
    }).catch(function(error) {
        console.error('Erreur restaurants', error);
    });
}

function loadTables() {
    getJson('/api/tables').then(function(data) {
        tables = data;
        let html = '<table>';
        html += '<tr><th>Table</th><th>Restaurant</th><th>Places</th><th>Statut</th></tr>';

        tables.forEach(function(t) {
            html += '<tr>';
            html += '<td>' + t.numTable + '</td>';
            html += '<td>' + t.restaurant + '</td>';
            html += '<td>' + t.nbPlaces + '</td>';
            html += '<td>' + t.statut + '</td>';
            html += '</tr>';
        });

        html += '</table>';
        document.getElementById('tables-container').innerHTML = html;
    }).catch(function(error) {
        console.error('Erreur tables', error);
    });
}

function loadReservations() {
    getJson('/api/reservations').then(function(data) {
        let html = '<table>';
        html += '<tr><th>Restaurant</th><th>Table</th><th>Client</th><th>Convives</th><th>Telephone</th></tr>';

        data.forEach(function(r) {
            html += '<tr>';
            html += '<td>' + r.restaurant + '</td>';
            html += '<td>' + r.numTable + '</td>';
            html += '<td>' + r.prenom + ' ' + r.nom + '</td>';
            html += '<td>' + r.nbConvives + '</td>';
            html += '<td>' + r.telephone + '</td>';
            html += '</tr>';
        });

        html += '</table>';
        document.getElementById('reservations-container').innerHTML = html;
    }).catch(function(error) {
        console.error('Erreur reservations', error);
    });
}

function reserver(restaurantId) {
    let restaurant = null;

    restaurants.forEach(function(r) {
        if (r.id == restaurantId) {
            restaurant = r;
        }
    });

    if (restaurant == null) {
        alert('Restaurant introuvable');
        return;
    }

    let tablesLibres = [];

    tables.forEach(function(t) {
        if (t.restaurantId == restaurantId && t.statut == 'Libre') {
            tablesLibres.push(t);
        }
    });

    if (tablesLibres.length == 0) {
        alert('Aucune table libre pour ce restaurant');
        return;
    }

    let message = 'Reservation pour ' + restaurant.nom + '\n\nTables libres :\n';

    tablesLibres.forEach(function(t) {
        message += 'Table ' + t.numTable + ' (' + t.nbPlaces + ' places)\n';
    });

    let numTable = Number(prompt(message + '\nNumero de table :'));
    if (numTable == 0) {
        return;
    }

    let tableChoisie = null;
    tablesLibres.forEach(function(t) {
        if (t.numTable == numTable) {
            tableChoisie = t;
        }
    });

    if (tableChoisie == null) {
        alert('Table invalide');
        return;
    }

    let nom = prompt('Nom :');
    if (nom == null || nom == '') return;

    let prenom = prompt('Prenom :');
    if (prenom == null || prenom == '') return;

    let nbConvives = Number(prompt('Nombre de convives :'));
    if (nbConvives <= 0) {
        alert('Nombre de convives invalide');
        return;
    }

    if (nbConvives > tableChoisie.nbPlaces) {
        alert('Il n y a pas assez de places');
        return;
    }

    let telephone = prompt('Telephone :');
    if (telephone == null || telephone == '') return;

    let reservation = {
        restaurantId: restaurantId,
        numTable: numTable,
        nom: nom,
        prenom: prenom,
        nbConvives: nbConvives,
        telephone: telephone
    };

    postJson('/api/reservations', reservation).then(function(resultat) {
        if (resultat.success) {
            alert('Reservation enregistree');
            loadTables();
            loadReservations();
        } else {
            alert(resultat.message);
        }
    }).catch(function(error) {
        console.error('Erreur reservation', error);
    });
}

function loadVelos() {
    getJson('/api/velos').then(function(stations) {
        // vider les stations de velos
        veloLayer.clearLayers();

        stations.forEach(function(s) {
            let popup = '<b>' + s.name + '</b><br>' +
                s.adresse + '<br>' +
                'Velos disponibles : ' + s.numBikesAvailable + '<br>' +
                'Places libres : ' + s.numDocksAvailable;

            ajouterPoint(veloLayer, s.lat, s.lon, 'green', popup);
        });
    }).catch(function(error) {
        console.error('Erreur velos', error);
    });
}

function loadIncidents() {
    getJson('/api/incidents').then(function(incidents) {
        incidentLayer.clearLayers();

        incidents.forEach(function(i) {
            let popup = '<b>' + i.cause + '</b><br>' +
                i.adresse + '<br>' +
                'Debut : ' + i.start + '<br>' +
                'Fin : ' + i.end;

            ajouterPoint(incidentLayer, i.lat, i.lon, 'red', popup);
        });
    }).catch(function(error) {
        console.error('Erreur incidents', error);
    });
}

window.addEventListener('load', function() {
    loadRestaurants();
    loadTables();
    loadReservations();
    loadVelos();
    loadIncidents();
});
