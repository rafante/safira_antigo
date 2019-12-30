class LicenseService {

    def getLicense() {
        return License.get(1)
    }

    def setLicense(params) {
        def license = License.get(1)
        if (!license) license = new License(id: 1)

        license.serial = params.serial
        license.save(flush: true, validate: false)

        return license
    }
}
