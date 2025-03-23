const { exec } = require('child_process');
const cron = require('node-cron');
const path = require('path');
const logger = require('./logger');

class BackupSystem {
    constructor() {
        this.backupDir = path.join(__dirname, '../backups');
        this.dbName = process.env.DB_NAME;
        this.dbUser = process.env.DB_USER;
        this.dbPass = process.env.DB_PASS;
    }

    async createBackup() {
        const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
        const filename = `backup-${timestamp}.gz`;
        const filePath = path.join(this.backupDir, filename);

        const command = `mongodump --uri="${process.env.MONGODB_URI}" --archive="${filePath}" --gzip`;

        return new Promise((resolve, reject) => {
            exec(command, (error, stdout, stderr) => {
                if (error) {
                    logger.error('백업 생성 실패:', error);
                    reject(error);
                    return;
                }
                logger.info('백업 생성 성공:', filename);
                resolve(filePath);
            });
        });
    }

    scheduleBackups() {
        // 매일 새벽 3시에 백업 실행
        cron.schedule('0 3 * * *', async () => {
            try {
                await this.createBackup();
                await this.cleanOldBackups();
            } catch (error) {
                logger.error('예약 백업 실패:', error);
            }
        });
    }

    async cleanOldBackups() {
        const maxAge = 30 * 24 * 60 * 60 * 1000; // 30일
        const command = `find ${this.backupDir} -type f -mtime +30 -delete`;
        
        exec(command, (error, stdout, stderr) => {
            if (error) {
                logger.error('오래된 백업 삭제 실패:', error);
                return;
            }
            logger.info('오래된 백업 삭제 완료');
        });
    }
}

module.exports = new BackupSystem(); 