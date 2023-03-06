export class Movie {
  id: string;
  title: string;
  imageType: string;
  image: string;
  summary: string;
  duration: number;
  imageName?: string;


  constructor(title: string, imageType: string, image: string, summary: string, duration: number, id: string, imageName?: string) {
    this.id = id;
    this.title = title;
    this.imageType = imageType;
    this.image = image;
    this.summary = summary;
    this.duration = duration
    this.imageName = imageName;
  }

}
