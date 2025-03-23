const cloudfront = require('aws-sdk/clients/cloudfront');
const logger = require('../utils/logger');

class CDNManager {
    constructor() {
        this.cloudfront = new cloudfront({
            accessKeyId: process.env.AWS_ACCESS_KEY_ID,
            secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY,
            region: process.env.AWS_REGION
        });
        this.distributionId = process.env.CLOUDFRONT_DISTRIBUTION_ID;
    }

    async invalidateCache(paths) {
        try {
            const params = {
                DistributionId: this.distributionId,
                InvalidationBatch: {
                    CallerReference: Date.now().toString(),
                    Paths: {
                        Quantity: paths.length,
                        Items: paths.map(path => `/${path}`)
                    }
                }
            };

            const result = await this.cloudfront.createInvalidation(params).promise();
            logger.info('CDN 캐시 무효화 성공:', result);
            return result;
        } catch (error) {
            logger.error('CDN 캐시 무효화 실패:', error);
            throw error;
        }
    }
}

module.exports = new CDNManager(); 