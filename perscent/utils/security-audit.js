const fs = require('fs').promises;
const path = require('path');
const crypto = require('crypto');
const logger = require('./logger');
const notification = require('./notification');

class SecurityAudit {
    constructor() {
        this.fileHashes = new Map();
        this.suspiciousIPs = new Set();
        this.failedLogins = new Map();
    }

    async performFileIntegrityCheck() {
        try {
            const files = await this.scanDirectory(process.cwd());
            for (const file of files) {
                const hash = await this.calculateFileHash(file);
                const previousHash = this.fileHashes.get(file);

                if (previousHash && previousHash !== hash) {
                    const alert = `파일 변조 감지: ${file}`;
                    logger.error(alert);
                    await notification.sendErrorAlert(new Error(alert));
                }

                this.fileHashes.set(file, hash);
            }
        } catch (error) {
            logger.error('파일 무결성 검사 실패:', error);
        }
    }

    async scanDirectory(dir) {
        const files = [];
        const entries = await fs.readdir(dir, { withFileTypes: true });

        for (const entry of entries) {
            const fullPath = path.join(dir, entry.name);
            if (entry.isDirectory()) {
                files.push(...await this.scanDirectory(fullPath));
            } else {
                files.push(fullPath);
            }
        }

        return files;
    }

    async calculateFileHash(filePath) {
        const content = await fs.readFile(filePath);
        return crypto.createHash('sha256').update(content).digest('hex');
    }

    trackLoginAttempt(ip, success) {
        if (!success) {
            const attempts = (this.failedLogins.get(ip) || 0) + 1;
            this.failedLogins.set(ip, attempts);

            if (attempts >= 5) {
                this.suspiciousIPs.add(ip);
                notification.sendErrorAlert(new Error(`의심스러운 로그인 시도 감지: ${ip}`));
            }
        } else {
            this.failedLogins.delete(ip);
        }
    }

    isIPBlocked(ip) {
        return this.suspiciousIPs.has(ip);
    }

    scheduleAudit() {
        // 매일 자정에 감사 수행
        const now = new Date();
        const night = new Date(
            now.getFullYear(),
            now.getMonth(),
            now.getDate() + 1,
            0, 0, 0
        );
        const msToMidnight = night.getTime() - now.getTime();

        setTimeout(() => {
            this.performAudit();
            setInterval(this.performAudit.bind(this), 24 * 60 * 60 * 1000);
        }, msToMidnight);
    }

    async performAudit() {
        await this.performFileIntegrityCheck();
        this.cleanupOldEntries();
        this.generateAuditReport();
    }

    cleanupOldEntries() {
        const oneDay = 24 * 60 * 60 * 1000;
        const now = Date.now();

        for (const [ip, timestamp] of this.failedLogins.entries()) {
            if (now - timestamp > oneDay) {
                this.failedLogins.delete(ip);
            }
        }
    }

    generateAuditReport() {
        const report = {
            timestamp: new Date().toISOString(),
            suspiciousIPs: Array.from(this.suspiciousIPs),
            failedLoginAttempts: Object.fromEntries(this.failedLogins),
            fileIntegrityStatus: Object.fromEntries(this.fileHashes)
        };

        logger.info('보안 감사 보고서:', report);
    }
}

module.exports = new SecurityAudit(); 