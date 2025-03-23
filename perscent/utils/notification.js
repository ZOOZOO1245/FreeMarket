const nodemailer = require('nodemailer');
const slack = require('@slack/webhook');
const logger = require('./logger');

class NotificationSystem {
    constructor() {
        this.emailTransporter = nodemailer.createTransport({
            host: process.env.SMTP_HOST,
            port: process.env.SMTP_PORT,
            secure: true,
            auth: {
                user: process.env.SMTP_USER,
                pass: process.env.SMTP_PASS
            }
        });

        this.slackWebhook = new slack.IncomingWebhook(process.env.SLACK_WEBHOOK_URL);
    }

    async sendErrorAlert(error) {
        try {
            // 이메일 알림
            await this.emailTransporter.sendMail({
                from: process.env.ALERT_EMAIL_FROM,
                to: process.env.ALERT_EMAIL_TO,
                subject: `[긴급] 시스템 오류 발생`,
                html: `
                    <h2>시스템 오류 발생</h2>
                    <p>시간: ${new Date().toISOString()}</p>
                    <p>오류: ${error.message}</p>
                    <pre>${error.stack}</pre>
                `
            });

            // 슬랙 알림
            await this.slackWebhook.send({
                text: '🚨 시스템 오류 발생',
                blocks: [
                    {
                        type: 'section',
                        text: {
                            type: 'mrkdwn',
                            text: `*시스템 오류 발생*\n시간: ${new Date().toISOString()}\n오류: ${error.message}`
                        }
                    }
                ]
            });
        } catch (notificationError) {
            logger.error('알림 전송 실패:', notificationError);
        }
    }

    async sendPerformanceAlert(metrics) {
        if (metrics.cpu > 80 || metrics.memory > 80) {
            await this.slackWebhook.send({
                text: '⚠️ 시스템 리소스 경고',
                blocks: [
                    {
                        type: 'section',
                        text: {
                            type: 'mrkdwn',
                            text: `*시스템 리소스 경고*\nCPU: ${metrics.cpu}%\n메모리: ${metrics.memory}%`
                        }
                    }
                ]
            });
        }
    }
}

module.exports = new NotificationSystem(); 