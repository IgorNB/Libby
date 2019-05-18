export const books = {
        name: 'Book |||| Books',
        validation: {
        },
        list: {},
        edit: {
            title: 'Book',
            bookFieldGroup: 'Book',
        },
        action: {
            COMMENT: "Add comment"
        },
        referenceManyFields: {
            comment: {
                body: "comment",
                rating: "rating",
                "createdBy": {
                    "id": "createdBy id",
                    "version": "createdBy version",
                    "name": "creator",
                    "email": "email",
                    "imageUrl": "avatar",
                    "emailVerified": "created By emailVerified",
                    "provider": "createdBy provider",
                    "providerId": "createdBy providerId"
                }
            }
        },
        fields: {
            "id": "id",
            "version": "version",
            "q": "q",
            "name": "name",
            "createdBy": {
                "id": "createdBy id",
                "version": "createdBy version",
                "name": "created by",
                "email": "email",
                "imageUrl": "createdBy imageUrl",
                "emailVerified": "created By emailVerified",
                "provider": "createdBy provider",
                "providerId": "createdBy providerId"
            },

            "work": {
                "id": "id",
                "version": "version",
            },
            "lang": {
                "id": "id",
                "code": "language"
            },
            "isbn": "isbn",
            "isbn13": "isbn13",
            "authors": "authors",
            "originalPublicationYear": "originalPublicationYear",
            "originalTitle": "original title",
            "title": "title",
            "averageRating": "rating",
            "ratingsCount": "ratings count",
            "imageUrl": "imageUrl",
            "smallImageUrl": "image"
        }
    }
;

