const fs = require('fs');
const https = require('https');

const sslConfig = {
    key: fs.readFileSync('/etc/letsencrypt/live/your-domain.com/privkey.pem'),
    cert: fs.readFileSync('/etc/letsencrypt/live/your-domain.com/fullchain.pem'),
    ca: fs.readFileSync('/etc/letsencrypt/live/your-domain.com/chain.pem')
};

const createHttpsServer = (app) => {
    return https.createServer(sslConfig, app);
};

module.exports = createHttpsServer; 